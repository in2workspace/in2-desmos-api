package es.in2.desmos.workflows;

import reactor.core.publisher.Flux;

public interface PublishWorkflow {
    Flux<Void> startPublishWorkflow(String processId);
}
