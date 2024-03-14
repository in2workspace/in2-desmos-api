package es.in2.desmos.blockchain.service;

import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.blockchain.model.DLTAdapterSubscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericDLTAdapterService {
    Mono<Void> createSubscription(String processId, DLTAdapterSubscription dltAdapterSubscription);
    Mono<Void> publishEvent(String processId, BlockchainEvent blockchainEvent);
    Flux<String> getEventsFromRange(String processId, long from, long to);
}

