package es.in2.desmos.api.facade;

import reactor.core.publisher.Mono;

public interface BrokerToBlockchainDataSyncPublisher {
    Mono<Void> createAndSynchronizeBlockchainEvents(String processId, String brokerEntity);
}
