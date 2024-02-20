package es.in2.desmos.api.facade.impl;

import es.in2.desmos.api.facade.BlockchainToBrokerDataSyncSynchronizer;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainToBrokerDataSyncSynchronizerImpl implements BlockchainToBrokerDataSyncSynchronizer {

    private final NotificationProcessorService notificationProcessorService;
    private final BrokerEntityRetrievalService brokerEntityRetrievalService;
    private final BrokerEntityPublisherService brokerEntityPublisherService;


    @Override
    public Mono<Void> retrieveAndSynchronizeEntityIntoBroker(String processId, BlockchainNotification blockchainNotification) {

        return notificationProcessorService.processBlockchainNotification(processId, blockchainNotification)
                // Try to retrieve the Entity from the source Broker
                .then(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, blockchainNotification))
                // Publish the retrieved Entity to own Broker
                .flatMap(retrievedEntity -> brokerEntityPublisherService
                        .publishRetrievedEntityToBroker(processId, retrievedEntity, blockchainNotification))
                .doOnSuccess(voidValue -> log.info("ProcessID: {} - Entity retrieval, validation, and publication completed", processId))
                .doOnError(e -> log.error("ProcessID: {} - Error retrieving, validating, and publishing entity", processId, e));
    }
}
