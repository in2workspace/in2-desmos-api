package es.in2.desmos.domain.services.api.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.repositories.AuditRecordRepository;
import es.in2.desmos.domain.services.api.AuditRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static es.in2.desmos.domain.utils.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditRecordServiceImpl implements AuditRecordService {

    private final ObjectMapper objectMapper;
    private final AuditRecordRepository auditRecordRepository;

    /**
     * Create a new AuditRecord with status CREATED or PUBLISHED and trader CONSUMER
     * from the data received in the broker notification.
     * If the BlockchainTxPayload is null, the status will be RECEIVED.
     */
    @Override
    public Mono<Void> buildAndSaveAuditRecordFromBrokerNotification(String processId, Map<String, Object> dataMap,
                                                                    AuditRecordStatus status, BlockchainTxPayload blockchainTxPayload) {
        // Extract the entity ID from the data location
        String entityId = dataMap.get("id").toString();
        // Get the most recent audit record for the entityId and get the most recent audit record overall
        return fetchMostRecentAuditRecord()
                .flatMap(lastAuditRecordRegistered -> {
                    try {
                        String entityHash;
                        String entityHashLink;
                        String dataLocation;
                        if (blockchainTxPayload == null) {
                            // status cases: RECEIVED
                            entityHash = "";
                            entityHashLink = "";
                            dataLocation = "";
                        } else {
                            // status cases: CREATED, PUBLISHED
                            entityHash = calculateSHA256(objectMapper.writeValueAsString(dataMap));
                            dataLocation = blockchainTxPayload.dataLocation();
                            entityHashLink = extractHashLinkFromDataLocation(dataLocation);
                        }
                        // Create the new audit record
                        AuditRecord auditRecord = AuditRecord.builder()
                                .id(UUID.randomUUID())
                                .processId(processId)
                                .createdAt(Timestamp.from(Instant.now()))
                                .entityId(entityId)
                                .entityType(dataMap.get("type").toString())
                                .entityHash(entityHash)
                                .entityHashLink(entityHashLink)
                                .dataLocation(dataLocation)
                                .status(status)
                                .trader(AuditRecordTrader.PRODUCER)
                                .hash("")
                                .hashLink("")
                                .newTransaction(true)
                                .build();
                        // Firstly, we calculate the hash of the entity without the hash and hashLink fields
                        String auditRecordHash = calculateSHA256(objectMapper.writeValueAsString(auditRecord));
                        auditRecord.setHash(auditRecordHash);
                        // Then, we calculate the hashLink of the entity concatenating the previous hashLink
                        // with the hash of the current entity
                        String auditRecordHashLink = calculateHashLink(lastAuditRecordRegistered.getHashLink(), auditRecordHash);
                        auditRecord.setHashLink(auditRecordHashLink);
                        return auditRecordRepository.save(auditRecord).then();
                    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                        return Mono.error(e);
                    }
                });
    }

    /**
     * Create a new AuditRecord with status RETRIEVED or PUBLISHED and trader CONSUMER
     * from the retrieved external broker entity in string format.
     * If the retrievedBrokerEntity is null or blank, the status will be RECEIVED.
     */
    @Override
    public Mono<Void> buildAndSaveAuditRecordFromBlockchainNotification(String processId, BlockchainNotification blockchainNotification,
                                                                        String retrievedBrokerEntity, AuditRecordStatus status) {
        // Extract the entity ID from the data location
        String entityId = extractEntityIdFromDataLocation(blockchainNotification.dataLocation());
        // Get the most recent audit record for the entityId and get the most recent audit record overall
        return fetchMostRecentAuditRecord()
                .flatMap(lastAuditRecordRegistered -> {
                    try {
                        String entityHash;
                        String entityHashLink;
                        if (retrievedBrokerEntity == null || retrievedBrokerEntity.isBlank()) {
                            // status cases: RECEIVED
                            entityHash = "";
                            entityHashLink = "";
                        } else {
                            // status cases: RETRIEVED, PUBLISHED
                            // We do not need to sort the fields of the retrievedBrokerEntity
                            // because these have already been sorted in the
                            // SubscribeWorkflowImpl.sortAttributesAlphabetically()
                            entityHash = calculateSHA256(retrievedBrokerEntity);
                            entityHashLink = calculateHashLink(lastAuditRecordRegistered.getEntityHashLink(), entityHash);
                        }
                        // Create the new audit record
                        AuditRecord auditRecord = AuditRecord.builder()
                                .id(UUID.randomUUID())
                                .processId(processId)
                                .createdAt(Timestamp.from(Instant.now()))
                                .entityId(entityId)
                                .entityType(blockchainNotification.eventType())
                                .entityHash(entityHash)
                                .entityHashLink(entityHashLink)
                                .dataLocation(blockchainNotification.dataLocation())
                                .status(status)
                                .trader(AuditRecordTrader.CONSUMER)
                                .hash("")
                                .hashLink("")
                                .newTransaction(true)
                                .build();
                        // Firstly, we calculate the hash of the entity without the hash and hashLink fields
                        String auditRecordHash = calculateSHA256(objectMapper.writeValueAsString(auditRecord));
                        auditRecord.setHash(auditRecordHash);
                        // Then, we calculate the hashLink of the entity concatenating the previous hashLink
                        // with the hash of the current entity
                        String auditRecordHashLink = calculateHashLink(lastAuditRecordRegistered.getHashLink(), auditRecordHash);
                        auditRecord.setHashLink(auditRecordHashLink);
                        return auditRecordRepository.save(auditRecord).then();
                    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                        return Mono.error(e);
                    }
                });
    }

    /**
     * Fetches the most recently registered audit record.
     *
     * @return A Mono containing the most recent AuditRecord, or Mono.empty() if none exists.
     */
    @Override
    public Mono<AuditRecord> fetchMostRecentAuditRecord() {
        return auditRecordRepository.findMostRecentAuditRecord();
    }

    /**
     * Retrieves the most recent audit record for the specified entity that is either published or deleted.
     *
     * @param processId The unique identifier of the process requesting the audit record.
     * @param entityId  The unique identifier of the entity for which to find the audit record.
     * @return A Mono emitting the latest published or deleted audit record for the given entity, if available.
     */
    @Override
    public Mono<AuditRecord> findLatestAuditRecordForEntity(String processId, String entityId) {
        log.debug("ProcessID: {} - Fetching latest audit record for entity ID: {}", processId, entityId);
        return auditRecordRepository.findMostRecentPublishedOrDeletedByEntityId(entityId);
    }

    @Override
    public Mono<AuditRecord> getLastPublishedAuditRecordForProducerByEntityId(String processId, String entityId) {
        log.debug("ProcessID: {} - Getting last audit record by entity id and producer: {}", processId, entityId);
        return auditRecordRepository.findLatestPublishedAuditRecordForProducerByEntityId(entityId);
    }

    @Override
    public Mono<String> fetchLatestProducerEntityHashByEntityId(String processId, String entityId) {
        return getLastPublishedAuditRecordForProducerByEntityId(processId, entityId)
                .flatMap(auditRecord -> auditRecord != null
                        ? Mono.just(auditRecord.getEntityHash())
                        : Mono.error(new NoSuchElementException()));
    }

}
