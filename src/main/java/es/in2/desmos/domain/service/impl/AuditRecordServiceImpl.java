package es.in2.desmos.domain.service.impl;

import es.in2.desmos.domain.model.AuditRecord;
import es.in2.desmos.domain.model.AuditRecordStatus;
import es.in2.desmos.domain.model.AuditRecordTrader;
import es.in2.desmos.domain.repository.AuditRecordRepository;
import es.in2.desmos.domain.service.AuditRecordService;
import es.in2.desmos.domain.util.AuditRecordFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditRecordServiceImpl implements AuditRecordService {

    private final AuditRecordRepository auditRecordRepository;

    @Override
    public Mono<Void> saveAuditRecord(String processId, AuditRecord auditRecord) {
        log.debug("ProcessID: {} - Saving audit record...", processId);
        return auditRecordRepository.save(auditRecord).then();
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
