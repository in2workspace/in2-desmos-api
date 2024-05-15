package es.in2.desmos.application.workflows.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.application.workflows.SubscribeWorkflow;
import es.in2.desmos.domain.exceptions.BrokerEntityRetrievalException;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static es.in2.desmos.domain.utils.ApplicationUtils.*;

/*
 *  Workflow steps:
 *  1. Get the event from the SubscribeQueue.
 *  2. Retrieve the BrokerEntity from the external Broker.
 *  3. Verify the data integrity of the retrieved entity.
 *  4. Publish the retrieved entity to the local Broker.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeWorkflowImpl implements SubscribeWorkflow {

    private final QueueService pendingSubscribeEventsQueue;
    private final BrokerPublisherService brokerPublisherService;
    private final AuditRecordService auditRecordService;
    private final DataSyncService dataSyncService;
    private final ObjectMapper objectMapper;
    private final ApiConfig apiConfig;

    @Override
    public Flux<Void> startSubscribeWorkflow(String processId) {
        log.info("ProcessID: {} - Starting the Subscribe Workflow...", processId);
        // Get the event stream for the events that need to be synchronized to the local broker
        return pendingSubscribeEventsQueue.getEventStream()
                // Get the next event (BlockchainNotification) from the queue
                .flatMap(pendingSubscribeQueueEventStream ->
                        Mono.just((BlockchainNotification) pendingSubscribeQueueEventStream.getEvent().get(0))
                                // verify that the DLTNotification is not null
                                .filter(Objects::nonNull)
                                // retrieve the entity from the source broker
                                .flatMap(blockchainNotification ->
                                        dataSyncService.getEntityFromExternalSource(processId, blockchainNotification)
                                                // verify the data integrity of the retrieved entity
                                                .flatMap(retrievedBrokerEntity ->
                                                        // Verify the integrity and consistency of the retrieved entity
                                                        dataSyncService.verifyRetrievedEntityData(processId, blockchainNotification, retrievedBrokerEntity)
                                                                // Build and save the audit record for RETRIEVED status
                                                                .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                                                                // Publish the retrieved entity to the local broker
                                                                .then(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                                                                // Build and save the audit record for PUBLISHED status
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
    // todo: to review
    public Mono<String> retrieveEntityFromExternalBroker(String processId, BlockchainNotification blockchainNotification) {
        log.debug("ProcessID: {} - Retrieving entity from the external broker...", processId);
        // Get the External Broker URL from the dataLocation
        String externalBrokerURL = extractContextBrokerUrlFromDataLocation(blockchainNotification.dataLocation());
        // WIP: If i change the local broker domain it tries to extract the entity from the local domain and not from the one specified on datalocation
        log.debug("ProcessID: {} - External Broker URL: {}", processId, externalBrokerURL);
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

    // todo: to review
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
            // WIP: Calculates the hashlink of the retrieved entity hash and the previous entity hash if the previous entity hash is different from the hashlink in the dataLocation
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

    // todo: to review
    public String sortAttributesAlphabetically(String retrievedBrokerEntity) throws JsonProcessingException {
        // Sort alphabetically the attributes of the retrieved broker entity
        JsonNode retrievedBrokerEntityJson = objectMapper.readTree(retrievedBrokerEntity);
        return objectMapper.writeValueAsString(retrievedBrokerEntityJson);
    }

}
