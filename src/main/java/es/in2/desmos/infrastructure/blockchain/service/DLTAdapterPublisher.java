package es.in2.desmos.infrastructure.blockchain.service;

import es.in2.desmos.domain.model.DLTEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DLTAdapterPublisher {
    Mono<Void> publishBlockchainEvent(String processId, DLTEvent dltEvent);
    Flux<String> getEventsFromRange(String processId, long from, long to);
}
