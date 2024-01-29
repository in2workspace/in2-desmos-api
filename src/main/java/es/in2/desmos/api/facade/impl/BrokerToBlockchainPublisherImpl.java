package es.in2.desmos.api.facade.impl;

import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
import es.in2.desmos.api.model.BrokerNotification;
import es.in2.desmos.api.service.BlockchainEventCreatorService;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.blockchain.service.BlockchainAdapterEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerToBlockchainPublisherImpl implements BrokerToBlockchainPublisher {

    private final NotificationProcessorService notificationProcessorService;
    private final BlockchainEventCreatorService blockchainEventCreatorService;
    private final BlockchainAdapterEventPublisher blockchainAdapterEventPublisher;

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
