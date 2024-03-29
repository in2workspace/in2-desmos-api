package es.in2.desmos.infrastructure.blockchain.service;

import es.in2.desmos.domain.model.DLTEvent;
import es.in2.desmos.infrastructure.blockchain.model.DLTAdapterSubscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericDLTAdapterService {
    Mono<Void> createSubscription(String processId, DLTAdapterSubscription dltAdapterSubscription);
    Mono<Void> publishEvent(String processId, DLTEvent dltEvent);
    Flux<String> getEventsFromRange(String processId, long from, long to);
}

