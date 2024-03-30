package es.in2.desmos.domain.service;

import es.in2.desmos.domain.model.AuditRecord;
import es.in2.desmos.domain.model.AuditRecordStatus;
import es.in2.desmos.domain.model.AuditRecordTrader;
import es.in2.desmos.domain.model.BlockchainNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuditRecordService {

    Mono<Map<String, Object>> buildAndSaveAuditRecordFromBrokerNotification(String processId, Map<String, Object> dataMap, AuditRecordStatus status, AuditRecordTrader trader);

    Mono<BlockchainNotification> buildAndSaveAuditRecordFromBlockchainNotification(String processId, BlockchainNotification blockchainNotification, AuditRecordStatus status, AuditRecordTrader trader);

    Mono<Map<String, Object>> buildAndSaveAuditRecord(String processId, Map<String, Object> dataMap, AuditRecordStatus status, AuditRecordTrader trader);

    Mono<AuditRecord> fetchMostRecentAuditRecord();

    Flux<AuditRecord> getAllAuditRecords(String processId);

    Mono<AuditRecord> findLatestAuditRecordForEntity(String processId, String entityId);

    Mono<AuditRecord> getLastPublishedAuditRecordForProducerByEntityId(String processId, String entityId);

    Mono<String> fetchLatestProducerEntityHashByEntityId(String processId, String entityId);

}
