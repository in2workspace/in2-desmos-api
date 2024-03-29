package es.in2.desmos.domain.service;

import es.in2.desmos.domain.model.AuditRecord;
import es.in2.desmos.domain.model.AuditRecordStatus;
import es.in2.desmos.domain.model.AuditRecordTrader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuditRecordService {
    Mono<Void> saveAuditRecord(String processId, AuditRecord auditRecord);
    Mono<AuditRecord> fetchMostRecentAuditRecord();
    Flux<AuditRecord> getAllAuditRecords(String processId);
    Mono<AuditRecord> findLatestAuditRecordForEntity(String processId, String entityId);
    Mono<AuditRecord> getLastPublishedAuditRecordForProducerByEntityId(String processId, String entityId);
    Mono<String> fetchLatestProducerEntityHashByEntityId(String processId, String entityId);
}
