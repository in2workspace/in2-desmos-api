package es.in2.desmos.domain.services.api;

import es.in2.desmos.domain.models.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface AuditRecordService {

    Mono<Void> buildAndSaveAuditRecordFromBrokerNotification(String processId, Map<String, Object> dataMap, AuditRecordStatus status, BlockchainTxPayload blockchainTxPayload);

    Mono<Void> buildAndSaveAuditRecordFromBlockchainNotification(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity, AuditRecordStatus status);

    Mono<Void> buildAndSaveAuditRecordForSubEntity(String processId, String entityId, String entityType,
                                                   String retrievedBrokerEntity,
                                                   AuditRecordStatus status);

    Mono<Void> buildAndSaveAuditRecordFromDataSync(String processId, String issuer, MVAuditServiceEntity4DataNegotiation mvAuditServiceEntity4DataNegotiation, AuditRecordStatus status);

    Mono<AuditRecord> fetchMostRecentAuditRecord();

    Mono<AuditRecord> findMostRecentRetrievedOrDeletedByEntityId(String processId, String entityId);

    Mono<AuditRecord> getLastPublishedAuditRecordForProducerByEntityId(String processId, String entityId);

    Mono<String> fetchLatestProducerEntityHashLinkByEntityId(String processId, String entityId);

    Mono<AuditRecord> findLatestConsumerPublishedAuditRecordByEntityId(String processId, String entityId);

    Mono<AuditRecord> findLatestConsumerPublishedAuditRecord(String processId);

    Mono<List<MVAuditServiceEntity4DataNegotiation>> findCreateOrUpdateAuditRecordsByEntityIds(String processId, String entityType, Mono<List<String>> entityIdsMono);
}