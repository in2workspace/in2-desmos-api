package es.in2.desmos.domain.service;

import es.in2.desmos.domain.model.EventQueue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QueueService {
    Mono<Void> enqueueEvent(EventQueue event);

    Flux<EventQueue> getEventStream();
}
