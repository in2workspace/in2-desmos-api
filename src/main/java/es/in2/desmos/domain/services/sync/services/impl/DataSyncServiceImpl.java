package es.in2.desmos.domain.services.sync.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.domain.exceptions.BrokerEntityRetrievalException;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;

import static es.in2.desmos.domain.utils.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncServiceImpl implements DataSyncService {

    private final ApiConfig apiConfig;
    private final ObjectMapper objectMapper;
    private final AuditRecordService auditRecordService;

    /*
     *  Workflow steps:
     *  1. Get all ProductOffering ID entities from the local Broker.
     *  2. Send a POST request to the configured external Broker with the ProductOffering ID entity list.
     *  3. Compare the response with the list of ProductOffering ID entities from the local Broker.
     *  4. If the response contains new ProductOffering ID entities,
     *  send a request to the external Broker to get the new ProductOfferings and its related entities.
     *  5. Publish the new ProductOfferings and its related entities to the local Broker.
     */
    @Override
    public Mono<Void> synchronizeData(String processId) {
        log.debug("ProcessID: {} - Synchronizing data...", processId);
        return Mono.empty();
    }

    /*
     *  This method retrieves the entity from the external broker executing the URL provided in the dataLocation
     *  field of the BlockchainNotification.
     *  If the entity is successfully retrieved, the method follows by checking the data integrity of itself.
     */
    @Override
    public Mono<String> getEntityFromExternalSource(String processId, BlockchainNotification blockchainNotification) {
        log.debug("ProcessID: {} - Retrieving entity from the external broker...", processId);
        // Get the External Broker URL from the dataLocation
        String externalBrokerURL = extractContextBrokerUrlFromDataLocation(blockchainNotification.dataLocation());
        log.debug("ProcessID: {} - External Broker URL: {}", processId, externalBrokerURL);
        // Retrieve entity from the External Broker

        return apiConfig.webClient()
                .get()
                .uri(externalBrokerURL)
                .accept(MediaType.APPLICATION_JSON)
                // todo: need to add authorization header to the request when it will be implemented in DOME (M2M Communication)
                .header("Authorization", "Bearer ")
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

    @Override
    public Mono<String> verifyRetrievedEntityData(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity) {
        // Verify Data Integrity of the Retrieved Entity
        return verifyRetrievedEntityDataIntegrity(processId, blockchainNotification, retrievedBrokerEntity)
                // Verify Data Consistency of the Retrieved Entity
                .then(verifyRetrievedEntityDataConsistency(processId, blockchainNotification, retrievedBrokerEntity));
    }

    // Verify Data Integrity of the Retrieved Entity (Focuses on preventing corruption and ensuring the data remains accurate and unaltered.)
    private Mono<String> verifyRetrievedEntityDataIntegrity(String processId,
                                                            BlockchainNotification blockchainNotification,
                                                            String retrievedBrokerEntity) {
        log.debug("ProcessID: {} - Verifying the data integrity of the retrieved entity...", processId);
        try {
            // Get the hash of the retrieved entity
            String retrievedEntityHash = calculateSHA256(sortAttributesAlphabetically(retrievedBrokerEntity));
            // To verify the data integrity: hash(previousEntityHash + retrievedEntityHash) = dataLocationHashLink
            String previousEntityHash = blockchainNotification.previousEntityHash().substring(2);
            String dataLocationHashLink = extractHashLinkFromDataLocation(blockchainNotification.dataLocation());
            if(dataLocationHashLink.equals(previousEntityHash)) {
                // It is the first entity in the chain, and we need to verify that
                // the hash of the retrieved entity is equal to the hash in the dataLocation
                if(!retrievedEntityHash.equals(dataLocationHashLink)) {
                    log.error("ProcessID: {} - Error occurred while verifying the data integrity of the retrieved entity: Hash verification failed", processId);
                    return Mono.error(new HashLinkException("Hash verification failed"));
                }
            } else {
                // It is not the first entity in the chain, and we need to verify that
                // the hash of the retrieved entity plus the previousEntitytHash is equal
                // to the hashLink in the dataLocation
                String calculatedEntityHasLink = calculateHashLink(previousEntityHash, retrievedEntityHash);
                if(!calculatedEntityHasLink.equals(dataLocationHashLink)) {
                    log.error("ProcessID: {} - Error occurred while verifying the data integrity of the retrieved entity: HashLink verification failed", processId);
                    return Mono.error(new HashLinkException("HashLink verification failed"));
                }
            }
            return Mono.just(retrievedBrokerEntity);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.warn("ProcessID: {} - Error occurred while sorting the attributes of the retrieved entity: {}", processId, e.getMessage());
            return Mono.error(new HashLinkException("Integrity of the retrieved entity verification failed"));
        }
    }

    // Verify Data Consistency of the Retrieved Entity (Ensures that data does not contradict itself and remains synchronized across different parts of the system.)
    private Mono<String> verifyRetrievedEntityDataConsistency(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity) {
        /*
         * 1. Buscamos en la BBDD si existe un registro con mismo ID de entidad (Consumer)
         * 2. Si existe, capturamos el hash de la entidad con estado (Published), esto es, aquella que finalizó
         * su proceso de integración con el Context Broker local satisfactoriamente.
         * 3. Validamos que el hash de la entidad de la BBDD corresponda con el previousHash de la notificación.
         * 4. Si corresponde, la consistencia es válida y no hemos perdido ningún dato entre ambos procesos.
         */
        return auditRecordService.findLatestConsumerPublishedAuditRecord(blockchainNotification.entityId())
                .flatMap(auditRecords -> {
                    // If there are audit records, we need to start querying the DLT Adapter from the last published audit record
                    return verifyDataConsistency(auditRecords, blockchainNotification, retrievedBrokerEntity);

                })
                .switchIfEmpty(Mono.just(retrievedBrokerEntity));
    }

    private Mono<String> verifyDataConsistency(AuditRecord auditRecord, BlockchainNotification blockchainNotification, String retrievedBrokerEntity) {
        String previousEntityHash = blockchainNotification.previousEntityHash().substring(2);
        if(auditRecord.getEntityHashLink().equals(previousEntityHash)) {
            return Mono.just(retrievedBrokerEntity);
        } else {
            log.error("Error occurred while verifying the data consistency of the retrieved entity: Data consistency verification failed");
            return Mono.error(new HashLinkException("Data consistency verification failed"));
        }

    }

    private String sortAttributesAlphabetically(String retrievedBrokerEntity) throws JsonProcessingException {
        // Sort alphabetically the attributes of the retrieved broker entity
        JsonNode retrievedBrokerEntityJson = objectMapper.readTree(retrievedBrokerEntity);
        return objectMapper.writeValueAsString(retrievedBrokerEntityJson);
    }

}
