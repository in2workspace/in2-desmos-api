package es.in2.desmos.api.service;

import es.in2.desmos.api.model.EventQueue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QueueService {
    Mono<Void> enqueueEvent(EventQueue event);
    Flux<EventQueue> getEventStream();
}
