package es.in2.desmos.domain.services.api;

import es.in2.desmos.domain.models.*;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuditRecordService {

    Mono<Void> buildAndSaveAuditRecordFromBrokerNotification(String processId, Map<String, Object> dataMap, AuditRecordStatus status, BlockchainTxPayload blockchainTxPayload);

    Mono<Void> buildAndSaveAuditRecordFromBlockchainNotification(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity, AuditRecordStatus status);

    Mono<Void> buildAndSaveAuditRecordFromDataSync(String processId, String issuer, MVEntity4DataNegotiation mvEntity4DataNegotiation, AuditRecordStatus status);

    Mono<AuditRecord> fetchMostRecentAuditRecord();
    Mono<AuditRecord> findLatestAuditRecordForEntity(String processId, String entityId);

    Mono<AuditRecord> getLastPublishedAuditRecordForProducerByEntityId(String processId, String entityId);

    Mono<String> fetchLatestProducerEntityHashByEntityId(String processId, String entityId);
}
