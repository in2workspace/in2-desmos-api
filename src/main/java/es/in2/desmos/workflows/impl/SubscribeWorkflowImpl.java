package es.in2.desmos.workflows.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.configs.ApiConfig;
import es.in2.desmos.domain.exceptions.BrokerEntityRetrievalException;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.workflows.SubscribeWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import static es.in2.desmos.domain.utils.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeWorkflowImpl implements SubscribeWorkflow {

    /*
     *  Workflow steps:
     *  1. Get the event from the SubscribeQueue.
     *  2. Retrieve the BrokerEntity from the external Broker.
     *  3. Publish the retrieved entity to the local Broker.
     */

    private final ObjectMapper objectMapper;
    private final ApiConfig apiConfig;
    private final QueueService pendingSubscribeEventsQueue;
    private final BrokerPublisherService brokerPublisherService;
    private final AuditRecordService auditRecordService;

    @Override
    public Flux<Void> startSubscribeWorkflow() {
        log.info("Starting the Subscribe Workflow...");
        // Generate a processId
        String processId = UUID.randomUUID().toString();
        log.debug("Process of retrieving data from the external sources started with processID: {}", processId);
        // The dataRetrievalQueue is a QueueService object used to retrieve data from the external sources
        return pendingSubscribeEventsQueue.getEventStream()
                // get the next BlockchainNotification from the queue
                .flatMap(pendingSubscribeQueueEventStream ->
                        Mono.just((BlockchainNotification) pendingSubscribeQueueEventStream.getEvent().get(0))
                                // verify that the DLTNotification is not null
                                .filter(Objects::nonNull)
                                // retrieve the entity from the source broker
                                .flatMap(blockchainNotification ->
                                        retrieveEntityFromExternalBroker(processId, blockchainNotification)
                                                // verify the data integrity of the retrieved entity
                                                .flatMap(retrievedBrokerEntity ->
                                                        verifyRetrievedEntityDataIntegrity(processId, blockchainNotification, retrievedBrokerEntity)
                                                                // build and save the audit record for RETRIEVED status
                                                                .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                                                                // publish the retrieved entity to the local broker
                                                                .then(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                                                                // build and save the audit record for PUBLISHED status
                                                                .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.PUBLISHED))
                                                )
                                )
                                .doOnSuccess(success -> log.info("ProcessID: {} - Subscribe Workflow completed successfully.", processId))
                                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the Subscribe Workflow: {}", processId, error.getMessage())));
    }

    /*
     *  This method retrieves the entity from the external broker executing the URL provided in the dataLocation
     *  field of the BlockchainNotification.
     *  If the entity is successfully retrieved, the method follows by checking the data integrity of itself.
     */
    public Mono<String> retrieveEntityFromExternalBroker(String processId, BlockchainNotification blockchainNotification) {
        log.debug("ProcessID: {} - Retrieving entity from the external broker...", processId);
        // Get the External Broker URL from the dataLocation
        String externalBrokerURL = extractContextBrokerUrlFromDataLocation(blockchainNotification.dataLocation());
        // Retrieve entity from the External Broker
        return apiConfig.webClient()
                .get()
                .uri(externalBrokerURL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status != null && status.isSameCodeAs(HttpStatusCode.valueOf(200)),
                        clientResponse -> {
                            log.debug("ProcessID: {} - Entity retrieved successfully from the external broker", processId);
                            return Mono.empty();
                        })
                .onStatus(status -> status != null && status.is4xxClientError(),
                        clientResponse -> {
                            throw new BrokerEntityRetrievalException("Error occurred while retrieving entity from the external broker");
                        })
                .onStatus(status -> status != null && status.is5xxServerError(),
                        clientResponse -> {
                            throw new BrokerEntityRetrievalException("Error occurred while retrieving entity from the external broker");
                        })
                .bodyToMono(String.class);
    }

    public Mono<String> verifyRetrievedEntityDataIntegrity(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity) {
        log.debug("ProcessID: {} - Verifying the data integrity of the retrieved entity...", processId);
        try {
            // We want to alphabetically sort the attributes of the retrieved broker entity
            retrievedBrokerEntity = sortAttributesAlphabetically(retrievedBrokerEntity);
            // Calculate the SHA-256 hash of the retrieved broker entity
            String retrievedEntityHash = calculateSHA256(retrievedBrokerEntity);
            // Extract the hashLink (value) from the BlockchainNotification.dataLocation field.
            // This hashLink is expected to match with the new calculated hashLink, that is,
            // the hash resulting from the concatenation of the BlockchainNotification.previousHash field
            // and the calculated hash of the retrieved broker entity.
            // The previousHash field starts with "0x" and the hashLink in the dataLocation field does not.
            String previousEntityHash = blockchainNotification.previousEntityHash().substring(2);
            String expectedEntityHasLink = extractHashLinkFromDataLocation(blockchainNotification.dataLocation());
            String calculatedEntityHasLink = previousEntityHash.equals(extractHashLinkFromDataLocation(blockchainNotification.dataLocation()))
                    ? previousEntityHash : calculateHashLink(previousEntityHash, retrievedEntityHash);
            // If the calculated hashLink does not match the expected hashLink, an exception is thrown.
            log.debug("ProcessID: {} - Retrieved Entity Hash: {}", processId, retrievedEntityHash);
            log.debug("ProcessID: {} - Calculated HashLink: {}", processId, calculatedEntityHasLink);
            log.debug("ProcessID: {} - Expected HashLink: {}", processId, expectedEntityHasLink);
            if (!calculatedEntityHasLink.equals(expectedEntityHasLink)) {
                log.error("ProcessID: {} - Error occurred while verifying the data integrity of the retrieved entity: HashLink verification failed", processId);
                return Mono.error(new HashLinkException("HashLink verification failed"));
            }
            // If the hashLink verification is successful, the retrieved entity is returned.
            else {
                log.debug("ProcessID: {} - Data integrity of the retrieved entity verified successfully", processId);
                return Mono.just(retrievedBrokerEntity);
            }
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.error("ProcessID: {} - Error occurred while sorting the attributes of the retrieved entity: {}", processId, e.getMessage());
            return Mono.error(e);
        }
    }

    public String sortAttributesAlphabetically(String retrievedBrokerEntity) throws JsonProcessingException {
        // Sort alphabetically the attributes of the retrieved broker entity
        JsonNode retrievedBrokerEntityJson = objectMapper.readTree(retrievedBrokerEntity);
        return objectMapper.writeValueAsString(retrievedBrokerEntityJson);
    }

}
