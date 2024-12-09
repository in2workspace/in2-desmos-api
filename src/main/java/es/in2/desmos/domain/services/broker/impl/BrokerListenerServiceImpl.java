package es.in2.desmos.domain.services.broker.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.BrokerNotificationSelfGeneratedException;
import es.in2.desmos.domain.exceptions.JsonReadingException;
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
                        .flatMap(dataMap -> isBrokerNotificationSelfGenerated(processId, dataMap)
                                .flatMap(isSelfGenerated -> {
                                    if (isSelfGenerated) {
                                        log.info("ProcessID: {} - Broker Notification is self-generated.", processId);
                                        return Mono.empty();
                                    } else {
                                        System.out.println("AAA ProcessID: " + processId + " - Broker Notification is new.");
                                        return auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(processId, dataMap,
                                                        AuditRecordStatus.RECEIVED, null)
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
                                }));
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

    private Mono<Boolean> isBrokerNotificationSelfGenerated(String processId, Map<String, Object> dataMap) {
        String id = dataMap.get("id").toString();

        System.out.println("ProcessID: " + processId + " - SG check id: + " + id);

        return auditRecordService.findMostRecentRetrievedOrDeletedByEntityId(processId, id)
                .flatMap(auditRecordFound -> {
                    System.out.println("AAA ProcessID: " + processId + " - SG check Audit record found id: + " + auditRecordFound.getEntityId());

                    try {
                        String newEntityHash = calculateSHA256(objectMapper.writer().writeValueAsString(dataMap));
                        if (auditRecordFound.getEntityHash().equals(newEntityHash)) {

                            System.out.println("AAA ProcessID: " + processId + " - SG check sha256 equals id: + " + auditRecordFound.getEntityId() + " hash: " + auditRecordFound.getEntityHash());

                            return Mono.just(true);
                        }
                        System.out.println("AAA ProcessID: " + processId + " - SG check sha256 different id 1: + " + id + " hash: " + newEntityHash);
                        System.out.println("AAA ProcessID: " + processId + " - SG check sha256 different id 2: + " + auditRecordFound.getEntityId() + " hash: " + auditRecordFound.getEntityHash());

                        return Mono.just(false);
                    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                        log.warn("AAA ProcessID: {} - Error processing JSON: {}", processId, e.getMessage());
                        return Mono.error(new JsonReadingException("Error processsing Broker Notification JSON"));
                    }
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            System.out.println("AAA ProcessID: " + processId + " - SG check id: " + id + "No se encontraron elementos, devolviendo false.");
                            return Mono.just(false);
                        })
                );
    }
}
