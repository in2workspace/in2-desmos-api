package es.in2.desmos.api.facade.impl;

import es.in2.desmos.api.facade.BrokerToBlockchainDataSyncPublisher;
import es.in2.desmos.api.service.BlockchainEventCreatorService;
import es.in2.desmos.api.service.BrokerEntityProcessorService;
import es.in2.desmos.blockchain.service.DLTAdapterEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerToBlockchainDataSyncPublisherImpl implements BrokerToBlockchainDataSyncPublisher {

    private final BrokerEntityProcessorService brokerEntityProcessorService;
    private final BlockchainEventCreatorService blockchainEventCreatorService;
    private final DLTAdapterEventPublisher dltAdapterEventPublisher;

    @Override
    public Mono<Void> createAndSynchronizeBlockchainEvents(String processId, String brokerEntityId) {
        log.debug("Creating and synchronizing Blockchain Events for Broker Entity with id: {}", brokerEntityId);
        return brokerEntityProcessorService.processBrokerEntity(processId, brokerEntityId)
                .filter(Objects::nonNull)
                // Create a Blockchain Event -> BlockchainEventCreator
                .flatMap(dataMap -> blockchainEventCreatorService.createBlockchainEvent(processId, dataMap))
                // Publish the Blockchain Event into the Blockchain Node -> BlockchainEventPublisher
                .flatMap(blockchainEvent -> dltAdapterEventPublisher.publishBlockchainEvent(processId, blockchainEvent))
                .doOnSuccess(success -> log.info("Blockchain Event creation and publication completed."))
                .doOnError(error -> log.error("Error creating or publishing Blockchain Event"));
    }

}