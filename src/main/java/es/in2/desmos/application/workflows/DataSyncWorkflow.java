package es.in2.desmos.application.workflows;

import reactor.core.publisher.Flux;

public interface DataSyncWorkflow {
    Flux<Void> startDataSyncWorkflow(String processId);
}
