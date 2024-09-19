package es.in2.desmos.domain.services.broker.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.BrokerNotificationParserException;
import es.in2.desmos.domain.exceptions.BrokerNotificationSelfGeneratedException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static es.in2.desmos.domain.utils.ApplicationUtils.calculateSHA256;

@Slf4j
@Service
public class BrokerListenerServiceImpl implements BrokerListenerService {

    private final ObjectMapper objectMapper;
    private final BrokerAdapterService brokerAdapter;
    private final AuditRecordService auditRecordService;
    private final QueueService pendingPublishEventsQueue;

    public BrokerListenerServiceImpl(BrokerAdapterFactory brokerAdapterFactory, ObjectMapper objectMapper,
                                     AuditRecordService auditRecordService, QueueService pendingPublishEventsQueue) {
        this.brokerAdapter = brokerAdapterFactory.getBrokerAdapter();
        this.objectMapper = objectMapper;
        this.auditRecordService = auditRecordService;
        this.pendingPublishEventsQueue = pendingPublishEventsQueue;
    }

    @Override
    public Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription) {
        return brokerAdapter.createSubscription(processId, brokerSubscription);
    }

    @Override
    public Mono<Void> processBrokerNotification(String processId, BrokerNotification brokerNotification) {
        log.info("ProcessID: {} - Processing Broker Notification...", processId);
        // Validate BrokerNotification is not null and has data
        return
                getDataFromBrokerNotification(brokerNotification)
                        // Validate if BrokerNotification is from an external source or self-generated
                        .flatMap(dataMap -> isBrokerNotificationFromExternalSource(processId, dataMap))
                        // Create and AuditRecord with status RECEIVED
                        .flatMap(dataMap -> auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(processId, dataMap,
                                AuditRecordStatus.RECEIVED, null))
                        // Set priority for the pendingSubscribeEventsQueue event
                        .then(Mono.just(EventQueuePriority.MEDIUM))
                        // Enqueue BrokerNotification to DataPublicationQueue
                        .flatMap(eventQueuePriority -> pendingPublishEventsQueue.enqueueEvent(EventQueue.builder()
                                .event(Collections.singletonList(brokerNotification))
                                .priority(eventQueuePriority)
                                .build()))
                        .doOnSuccess(unused -> log.info("ProcessID: {} - Broker Notification processed successfully.", processId))
                        .doOnError(throwable -> {
                            if (throwable instanceof BrokerNotificationSelfGeneratedException) {
                                log.info("ProcessID: {} - Self-Generated Broker Notification. It does not need to do nothing.", processId);
                            } else {
                                log.error("ProcessID: {} - Error processing Broker Notification: {}", processId, throwable.getMessage());
                            }
                        });
    }

    @Override
    public Mono<String> getEntityById(String processId, String entityId) {
        return brokerAdapter.getEntityById(processId, entityId);
    }

    private Mono<Map<String, Object>> getDataFromBrokerNotification(BrokerNotification brokerNotification) {
        //Get data from brokerNotification
        Map<String, Object> dataMap = brokerNotification.data().get(0);
        return Mono.just(dataMap);
    }

    private Mono<Map<String, Object>> isBrokerNotificationFromExternalSource(String processId, Map<String, Object> dataMap) {
        String id = dataMap.get("id").toString();

        return waitForUnlock(processId, id)
                .then(auditRecordService.findLatestAuditRecordForEntity(processId, id)
                        .flatMap(auditRecordFound -> {
                            try {
                                String newEntityHash = calculateSHA256(objectMapper.writer().writeValueAsString(dataMap));
                                if (auditRecordFound.getEntityHash().equals(newEntityHash)) {
                                    return Mono.error(new BrokerNotificationSelfGeneratedException("BrokerNotification is self-generated"));
                                }
                            } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                                log.warn("ProcessID: {} - Error processing JSON: {}", processId, e.getMessage());
                                return Mono.error(new BrokerNotificationParserException("Error processing JSON"));
                            }
                            log.debug("ProcessID: {} - BrokerNotification is from external source", processId);
                            return Mono.just(dataMap);
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            log.debug("ProcessID: {} - No audit record found; assuming BrokerNotification is from external source", processId);
                            return Mono.just(dataMap);
                        })));
    }

    private Mono<Void> waitForUnlock(String processId, String id) {
        return auditRecordService.isAuditRecordUnlocked(processId, id)
                .flatMap(isAuditRecordUnlocked -> {
                    if (Boolean.TRUE.equals(isAuditRecordUnlocked)) {
                        return Mono.empty();
                    } else {
                        return Mono.delay(Duration.ofMillis(100))
                                .then(waitForUnlock(processId, id));
                    }
                });
    }

}
