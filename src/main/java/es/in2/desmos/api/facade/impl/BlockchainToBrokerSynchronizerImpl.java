package es.in2.desmos.api.facade.impl;

import es.in2.desmos.api.facade.BlockchainToBrokerSynchronizer;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.service.BrokerEntityPublisherService;
import es.in2.desmos.api.service.BrokerEntityRetrievalService;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.api.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainToBrokerSynchronizerImpl implements BlockchainToBrokerSynchronizer {

    private final NotificationProcessorService notificationProcessorService;
    private final BrokerEntityRetrievalService brokerEntityRetrievalService;
    private final BrokerEntityPublisherService brokerEntityPublisherService;
    private final QueueService blockchainToBrokerQueueService;

    @Override
    public Flux<Void> startProcessingEvents() {
        return blockchainToBrokerQueueService.getEventStream()
                .flatMap(eventQueue -> {
                    String processId = UUID.randomUUID().toString();
                    MDC.put("processId", processId);
                    if (eventQueue.getPriority().name().startsWith("RECOVER")) {
                        log.debug("Detected event from recover queue, processing...");
                        return brokerEntityPublisherService.publishRetrievedEntityToBroker(processId, (String) eventQueue.getEvent().get(1), (BlockchainNotification) eventQueue.getEvent().get(0))
                                .doOnSuccess(voidValue -> log.debug("Broker Entity Published Successfully"))
                                .onErrorResume(error -> {
                                    log.error("Error in processing, moving to next event");
                                    return Mono.empty();
                                });
                    }
                    return retrieveAndPublishEntityToBroker(processId,(BlockchainNotification) eventQueue.getEvent().get(0))
                            .doOnSuccess(voidValue -> log.debug("Broker Entity Published Successfully"))
                            .doOnError(error -> log.error("Error processing entity"))
                            .onErrorResume(error -> {
                                log.error("Error in processing, moving to next event");
                                return Mono.empty();
                            });
                });
    }
    @Override
    public Mono<Void> retrieveAndPublishEntityToBroker(String processId, BlockchainNotification blockchainNotification) {
        // Process the Blockchain Notification
        return notificationProcessorService.processBlockchainNotification(processId, blockchainNotification)
                // Try to retrieve the Entity from the source Broker
                .then(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, blockchainNotification))
                // Publish the retrieved Entity to own Broker
                .flatMap(retrievedEntity -> brokerEntityPublisherService
                        .publishRetrievedEntityToBroker(processId, retrievedEntity, blockchainNotification))
                .doOnSuccess(voidValue -> log.info("ProcessID: {} - Entity retrieval, validation, and publication completed", processId))
                .doOnError(e -> log.error("ProcessID: {} - Error retrieving, validating, and publishing entity", processId));
    }

}
