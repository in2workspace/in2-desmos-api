package es.in2.desmos.workflows;

import reactor.core.publisher.Flux;

public interface SubscribeWorkflow {
    Flux<Void> startSubscribeWorkflow();
}
