package es.in2.desmos.api.facade.impl;

import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.BrokerNotification;
import es.in2.desmos.api.service.BlockchainEventCreatorService;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.api.service.QueueService;
import es.in2.desmos.blockchain.service.DLTAdapterEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerToBlockchainPublisherImpl implements BrokerToBlockchainPublisher {

    private final NotificationProcessorService notificationProcessorService;
    private final BlockchainEventCreatorService blockchainEventCreatorService;
    private final DLTAdapterEventPublisher dltAdapterEventPublisher;
    private final QueueService brokerToBlockchainQueueService;

    @Override
    public Flux<Void> startProcessingEvents() {
        return brokerToBlockchainQueueService.getEventStream()
                .flatMap(eventQueue -> {
                    String processId = UUID.randomUUID().toString();
                    log.debug("Processing event with processId: {}", processId);
                    MDC.put("processId", processId);
                    if (eventQueue.getPriority().name().startsWith("RECOVER")) {
                        log.debug("Detected event from recover queue, processing...");
                        return dltAdapterEventPublisher.publishBlockchainEvent(processId, (BlockchainEvent) eventQueue.getEvent().get(0))
                                .doOnSuccess(voidValue -> log.debug("Blockchain Event Publishing Completed"))
                                .onErrorResume(error -> {
                                    log.error("Error in processing, moving to next event");
                                    return Mono.empty();
                                });
                    }
                    return processAndPublishBrokerNotificationToBlockchain(processId, (BrokerNotification) eventQueue.getEvent().get(0))
                            .doOnSuccess(voidValue -> log.debug("Blockchain Event Publishing Completed"))
                            .doOnError(error -> log.error("Error processing event"))
                            .onErrorResume(error -> {
                                log.error("Error in processing, moving to next event");
                                return Mono.empty();
                            });
                });
    }

    @Override
    public Mono<Void> processAndPublishBrokerNotificationToBlockchain(String processId, BrokerNotification brokerNotification) {
        // Process the notification received from the Broker -> BrokerNotificationProcessor
        return notificationProcessorService.processBrokerNotification(processId, brokerNotification)
                .filter(Objects::nonNull)
                // Create a Blockchain Event -> BlockchainEventCreator
                .flatMap(dataMap -> blockchainEventCreatorService.createBlockchainEvent(processId, dataMap))
                // Publish the Blockchain Event into the Blockchain Node -> BlockchainEventPublisher
                .flatMap(blockchainEvent -> dltAdapterEventPublisher.publishBlockchainEvent(processId, blockchainEvent))
                .doOnSuccess(success -> log.info("Blockchain Event creation and publication completed."))
                .doOnError(error -> log.error("Error creating or publishing Blockchain Event: {}", error.getMessage(), error));
    }

}