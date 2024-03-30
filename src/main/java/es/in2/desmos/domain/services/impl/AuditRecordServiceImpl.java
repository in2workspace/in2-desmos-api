package es.in2.desmos.domain.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.AuditRecordTrader;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.repositories.AuditRecordRepository;
import es.in2.desmos.domain.services.AuditRecordService;
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

    @Override
    public Mono<Map<String,Object>> buildAndSaveAuditRecordFromBrokerNotification(String processId, Map<String, Object> dataMap,
                                                                                  AuditRecordStatus status, AuditRecordTrader trader) {
        return createAuditRecord(processId, dataMap, status, trader)
                .flatMap(auditRecordRepository::save)
                .thenReturn(dataMap);
    }

    private Mono<AuditRecord> createAuditRecordFromBrokerNotification(String processId, Map<String, Object> dataMap, AuditRecordStatus status,
                                                AuditRecordTrader trader) {
        String entityId = (String) dataMap.get("id");
        return Mono.zip(findLatestAuditRecordForEntity(processId, entityId), fetchMostRecentAuditRecord())
                .flatMap(tuple -> {
                    try {
                        // Get the most recent audit record for the entity
                        AuditRecord auditRecordFound = tuple.getT1();
                        // Get the most recent audit record overall
                        AuditRecord lastAuditRecordRegistered = tuple.getT2();
                        // Create the new audit record
                        String entityHash = calculateSHA256(objectMapper.writeValueAsString(dataMap));
                        AuditRecord auditRecord = AuditRecord.builder()
                                .id(UUID.randomUUID())
                                .processId(processId)
                                .createdAt(Timestamp.from(Instant.now()))
                                .entityId(entityId)
                                .entityType((String) dataMap.get("type"))
                                .entityHash(entityHash)
                                .entityHashLink(calculateHashLink(auditRecordFound.getEntityHashLink(), entityHash))
                                .dataLocation("")
                                .status(status)
                                .trader(trader)
                                .hash("")
                                .hashLink("")
                                .newTransaction(true)
                                .build();
                        // Set Audit Record hash
                        String auditRecordHash = calculateSHA256(objectMapper.writeValueAsString(auditRecord));
                        auditRecord.setHash(auditRecordHash);
                        String auditRecordHashLink = calculateHashLink(lastAuditRecordRegistered.getHashLink(), auditRecordHash);
                        auditRecord.setHashLink(auditRecordHashLink);
                        return Mono.just(auditRecord);
                    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                        return Mono.error(e);
                    }
                });
    }

    @Override
    public Mono<BlockchainNotification> buildAndSaveAuditRecordFromBlockchainNotification(String processId, BlockchainNotification blockchainNotification,
                                                                                      AuditRecordStatus status, AuditRecordTrader trader) {
//        return createAuditRecord(processId, dataMap, status, trader)
//                .flatMap(auditRecordRepository::save)
//                .thenReturn(dataMap);
        return null;
    }

    // fixme
//    private Mono<AuditRecord> createAuditRecordFromBlockchainNotification(String processId, BlockchainNotification blockchainNotification,
//                                                                          AuditRecordStatus status, AuditRecordTrader trader) {
//        String entityId = extractEntityIdFromDataLocation(blockchainNotification.dataLocation());
//        return Mono.zip(findLatestAuditRecordForEntity(processId, entityId), fetchMostRecentAuditRecord())
//                .flatMap(tuple -> {
//                    try {
//                        // Get the most recent audit record for the entity
//                        AuditRecord auditRecordFound = tuple.getT1();
//                        // Get the most recent audit record overall
//                        AuditRecord lastAuditRecordRegistered = tuple.getT2();
//                        // Create the new audit record
//                        String entityHash = calculateSHA256(objectMapper.writeValueAsString(dataMap));
//                        AuditRecord auditRecord = AuditRecord.builder()
//                                .id(UUID.randomUUID())
//                                .processId(processId)
//                                .createdAt(Timestamp.from(Instant.now()))
//                                .entityId(entityId)
//                                .entityType((String) dataMap.get("type"))
//                                .entityHash(entityHash)
//                                .entityHashLink(calculateHashLink(auditRecordFound.getEntityHashLink(), entityHash))
//                                .dataLocation("")
//                                .status(status)
//                                .trader(trader)
//                                .hash("")
//                                .hashLink("")
//                                .newTransaction(true)
//                                .build();
//                        // Set Audit Record hash
//                        String auditRecordHash = calculateSHA256(objectMapper.writeValueAsString(auditRecord));
//                        auditRecord.setHash(auditRecordHash);
//                        String auditRecordHashLink = calculateHashLink(lastAuditRecordRegistered.getHashLink(), auditRecordHash);
//                        auditRecord.setHashLink(auditRecordHashLink);
//                        return Mono.just(auditRecord);
//                    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
//                        return Mono.error(e);
//                    }
//                });
//    }







    @Override
    public Mono<Map<String, Object>> buildAndSaveAuditRecord(String processId, Map<String, Object> dataMap, AuditRecordStatus status, AuditRecordTrader trader) {
        return createAuditRecord(processId, dataMap, status, trader)
                .flatMap(auditRecordRepository::save)
                .thenReturn(dataMap);
    }

    private Mono<AuditRecord> createAuditRecord(String processId, Map<String, Object> dataMap, AuditRecordStatus status,
                                                AuditRecordTrader trader) {
        String entityId = (String) dataMap.get("id");
        return Mono.zip(findLatestAuditRecordForEntity(processId, entityId), fetchMostRecentAuditRecord())
                .flatMap(tuple -> {
                    try {
                        // Get the most recent audit record for the entity
                        AuditRecord auditRecordFound = tuple.getT1();
                        // Get the most recent audit record overall
                        AuditRecord lastAuditRecordRegistered = tuple.getT2();
                        // Create the new audit record
                        String entityHash = calculateSHA256(objectMapper.writeValueAsString(dataMap));
                        AuditRecord auditRecord = AuditRecord.builder()
                                .id(UUID.randomUUID())
                                .processId(processId)
                                .createdAt(Timestamp.from(Instant.now()))
                                .entityId(entityId)
                                .entityType((String) dataMap.get("type"))
                                .entityHash(entityHash)
                                .entityHashLink(calculateHashLink(auditRecordFound.getEntityHashLink(), entityHash))
                                .dataLocation("")
                                .status(status)
                                .trader(trader)
                                .hash("")
                                .hashLink("")
                                .newTransaction(true)
                                .build();
                        // Set Audit Record hash
                        String auditRecordHash = calculateSHA256(objectMapper.writeValueAsString(auditRecord));
                        auditRecord.setHash(auditRecordHash);
                        String auditRecordHashLink = calculateHashLink(lastAuditRecordRegistered.getHashLink(), auditRecordHash);
                        auditRecord.setHashLink(auditRecordHashLink);
                        return Mono.just(auditRecord);
                    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                        return Mono.error(e);
                    }
                });
    }

    @Override
    public Flux<AuditRecord> getAllAuditRecords(String processId) {
        log.debug("ProcessID: {} - Getting all audit records...", processId);
        return auditRecordRepository.findAll();
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
