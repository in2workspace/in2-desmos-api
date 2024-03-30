package es.in2.desmos.domain.services;

import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.AuditRecordTrader;
import es.in2.desmos.domain.models.BlockchainNotification;
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
