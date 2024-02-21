package es.in2.desmos.blockchain.service;

import es.in2.desmos.api.model.BlockchainEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DLTAdapterEventPublisher {

    Mono<Void> publishBlockchainEvent(String processId, BlockchainEvent blockchainEvent);

    Flux<String> getEventsFromRange(String processId, long from, long to);

}
