package es.in2.desmos.api.facade.impl;

import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.BrokerNotification;
import es.in2.desmos.api.service.BlockchainEventCreatorService;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.api.service.QueueService;
import es.in2.desmos.blockchain.service.BlockchainAdapterEventPublisher;
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
    private final BlockchainAdapterEventPublisher blockchainAdapterEventPublisher;
    private final QueueService brokerToBlockchainQueueService;


    @Override
    public Flux<Void> startProcessingEvents() {
        return brokerToBlockchainQueueService.getEventStream()
                .flatMap(eventQueue -> {
                    String processId = UUID.randomUUID().toString();
                    MDC.put("processId", processId);
                    return processAndPublishBrokerNotificationToBlockchain(processId,(BrokerNotification) eventQueue.getEvent().get(0))
                            .doOnSuccess(voidValue -> log.debug("Blockchain Event Published Successfully"));
                })
                .onErrorResume(error -> {
                    log.error("Error processing event: {}", error.getMessage(), error);
                    return Mono.empty();
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
                .flatMap(blockchainEvent -> blockchainAdapterEventPublisher.publishBlockchainEvent(processId, blockchainEvent))
                .doOnSuccess(success -> log.info("Blockchain Event created and published successfully."))
                .doOnError(error -> log.error("Error creating or publishing Blockchain Event: {}", error.getMessage(), error));
    }

}
