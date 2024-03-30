package es.in2.desmos.services.broker.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.BrokerNotificationParserException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.AuditRecordService;
import es.in2.desmos.domain.services.QueueService;
import es.in2.desmos.services.broker.BrokerListenerService;
import es.in2.desmos.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.services.broker.adapter.factory.BrokerAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

import static es.in2.desmos.domain.utils.ApplicationUtils.calculateSHA256;

@Slf4j
@Service
public class BrokerListenerServiceImpl implements BrokerListenerService {

    private final BrokerAdapterService brokerAdapter;
    private final ObjectMapper objectMapper;
    private final AuditRecordService auditRecordService;
    private final QueueService dataPublicationQueue;

    public BrokerListenerServiceImpl(BrokerAdapterFactory brokerAdapterFactory, ObjectMapper objectMapper,
                                     AuditRecordService auditRecordService, QueueService dataPublicationQueue) {
        this.brokerAdapter = brokerAdapterFactory.getBrokerAdapter();
        this.objectMapper = objectMapper;
        this.auditRecordService = auditRecordService;
        this.dataPublicationQueue = dataPublicationQueue;
    }

    @Override
    public Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription) {
        return brokerAdapter.createSubscription(processId, brokerSubscription);
    }

    @Override
    public Mono<Void> processBrokerNotification(String processId, BrokerNotification brokerNotification) {
        // Validate BrokerNotification is not null and has data
        return validateBrokerNotification(brokerNotification)
                // Validate if BrokerNotification is from an external source or self-generated
                .flatMap(dataMap -> isBrokerNotificationFromExternalSource(processId, dataMap))
                // Build and save AuditRecord
                .flatMap(dataMap -> auditRecordService.buildAndSaveAuditRecord(processId, dataMap,
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

}
