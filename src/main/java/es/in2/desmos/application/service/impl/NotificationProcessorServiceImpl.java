package es.in2.desmos.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.application.service.NotificationProcessorService;
import es.in2.desmos.domain.exception.BrokerNotificationParserException;
import es.in2.desmos.domain.model.*;
import es.in2.desmos.domain.service.AuditRecordService;
import es.in2.desmos.domain.service.QueueService;
import es.in2.desmos.domain.util.AuditRecordFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static es.in2.desmos.domain.util.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessorServiceImpl implements NotificationProcessorService {

    private final ObjectMapper objectMapper;
    private final AuditRecordService auditRecordService;
    private final AuditRecordFactory auditRecordFactory;
    private final QueueService dataPublicationQueue;
    private final QueueService dataRetrievalQueue;

    @Override
    public Mono<Void> processBrokerNotification(String processId, BrokerNotification brokerNotification) {
        // Validate BrokerNotification is not null and has data
        return validateBrokerNotification(brokerNotification)
                // Validate if BrokerNotification is from an external source or self-generated
                .flatMap(dataMap -> isBrokerNotificationFromExternalSource(processId, dataMap))
                // Build and save AuditRecord
                .flatMap(dataMap -> auditRecordFactory.buildAndSaveAuditRecord(processId, dataMap,
                        AuditRecordStatus.RECEIVED, AuditRecordTrader.PRODUCER)
                )
                // Set priority for BrokerNotification
                .flatMap(data -> setPriorityForBrokerNotification(processId, data))
                // Enqueue BrokerNotification to DataPublicationQueue
                .flatMap(eventQueuePriority -> dataPublicationQueue.enqueueEvent(EventQueue.builder()
                        .event(Collections.singletonList(brokerNotification))
                        .priority(eventQueuePriority)
                        .build()));
    }

    @Override
    public Mono<Void> processDLTNotification(String processId, DLTNotification dltNotification) {
        // Validate DLTNotification is not null
        return validateDLTNotification(dltNotification)
                // Save AuditRecord
                .then(auditRecordService.saveAuditRecord(processId, AuditRecord.builder()
                        .id(UUID.randomUUID())
                        .processId(processId)
                        .createdAt(Timestamp.from(Instant.now()))
                        .entityId(extractEntityIdFromDataLocation(dltNotification.dataLocation()))
                        .entityType(dltNotification.eventType())
                        .entityHash("")
                        .entityHashLink(extractHashLinkFromDataLocation(dltNotification.dataLocation()))
                        .dataLocation(dltNotification.dataLocation())
                        .status(AuditRecordStatus.RECEIVED)
                        .trader(AuditRecordTrader.CONSUMER)
                        .hash("")
                        .hashLink("")
//                        .newTransaction(true)
                        .build()))
                // Set priority for DLTNotification
                .then(setPriorityForDLTNotification(dltNotification))
                // Enqueue DLTNotification to DataRetrievalQueue
                .flatMap(eventQueuePriority -> dataRetrievalQueue.enqueueEvent(EventQueue.builder()
                        .event(Collections.singletonList(dltNotification))
                        .priority(eventQueuePriority)
                        .build()));
    }

    private Mono<Map<String, Object>> validateBrokerNotification(BrokerNotification brokerNotification) {
        // Validate brokerNotification
        checkIfBrokerNotificationIsNullOrEmpty(brokerNotification);
        // Validate brokerNotification data
        Map<String, Object> dataMap = brokerNotification.data().get(0);
        checkIfBrokerNotificationDataIsNullOrHasNullId(dataMap);
        return Mono.just(dataMap);
    }

    private void checkIfBrokerNotificationIsNullOrEmpty(BrokerNotification brokerNotification) {
        if (brokerNotification == null || brokerNotification.data().isEmpty()) {
            throw new IllegalArgumentException("Invalid BrokerNotificationDTO");
        }
    }

    private void checkIfBrokerNotificationDataIsNullOrHasNullId(Map<String, Object> dataMap) {
        if (dataMap == null || dataMap.get("id") == null) {
            throw new IllegalArgumentException("Invalid dataMap in BrokerNotificationDTO");
        }
    }

    private Mono<Map<String, Object>> isBrokerNotificationFromExternalSource(String processId, Map<String, Object> dataMap) {
        return auditRecordService.findLatestAuditRecordForEntity(processId, dataMap.get("id").toString())
                .flatMap(auditRecordFound -> {
                    try {
                        String newEntityHash = calculateSHA256(objectMapper.writer().writeValueAsString(dataMap));
                        if (auditRecordFound.getEntityHash().equals(newEntityHash)) {
                            log.debug("ProcessID: {} - BrokerNotification is self-generated", processId);
                            return Mono.empty();
                        }
                    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                        return Mono.error(new BrokerNotificationParserException("Error processing JSON", e));
                    }
                    log.debug("ProcessID: {} - BrokerNotification is from external source", processId);
                    return Mono.just(dataMap);
                })
                .doOnNext(map -> log.debug("ProcessID: {} - No transaction found; assuming BrokerNotification is from external source", processId));
    }

    private Mono<EventQueuePriority> setPriorityForBrokerNotification(String processId, Map<String, Object> dataMap) {
        if (dataMap.containsKey("deletedAt")) {
            return Mono.just(EventQueuePriority.PUBLICATION_DELETE);
        } else {
            return auditRecordService.findLatestAuditRecordForEntity(processId,
                    dataMap.get("id").toString()).flatMap(transaction -> Mono.just(EventQueuePriority.PUBLICATION_EDIT)).defaultIfEmpty(EventQueuePriority.PUBLICATION_PUBLISH);
        }
    }

    private Mono<Void> validateDLTNotification(DLTNotification dltNotification) {
        checkIfNotificationIsNullOrDataLocationIsEmpty(dltNotification);
        return Mono.empty();
    }

    private void checkIfNotificationIsNullOrDataLocationIsEmpty(DLTNotification dltNotification) {
        if (dltNotification == null || dltNotification.dataLocation().isEmpty()) {
            throw new IllegalArgumentException("Invalid Blockchain Notification");
        }
    }

    // TODO: Review this method -> check if all decisions are being made correctly
    private Mono<EventQueuePriority> setPriorityForDLTNotification(DLTNotification dltNotification) {
        EventQueuePriority eventQueuePriority = EventQueuePriority.PUBLICATION_PUBLISH;
        if (!checkIfHashLinkExistInDataLocation(dltNotification.dataLocation())) {
            eventQueuePriority = EventQueuePriority.PUBLICATION_DELETE;
        } else if (!Objects.equals(dltNotification.previousEntityHash(),
                "0x0000000000000000000000000000000000000000000000000000000000000000")) {
            eventQueuePriority = EventQueuePriority.PUBLICATION_EDIT;
        }
        return Mono.just(eventQueuePriority);
    }

}