package es.in2.desmos.api.facade;


import es.in2.desmos.api.model.BlockchainNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlockchainToBrokerSynchronizer {
    Flux<Void> startProcessingEvents();
    Mono<Void> retrieveAndPublishEntityToBroker(String processId, BlockchainNotification blockchainNotification);
}
