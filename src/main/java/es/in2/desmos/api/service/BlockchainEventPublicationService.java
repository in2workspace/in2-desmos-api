package es.in2.desmos.api.service;


import es.in2.desmos.api.model.BlockchainEvent;
import reactor.core.publisher.Mono;

public interface BlockchainEventPublicationService {
    Mono<Void> publishBlockchainEventIntoBlockchainNode(String processId, BlockchainEvent blockchainEvent);
}
