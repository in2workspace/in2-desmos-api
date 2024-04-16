package es.in2.desmos.workflows;

import reactor.core.publisher.Flux;

public interface BlockchainDataSyncWorkflow {
    Flux<Void> startBlockchainDataSyncWorkflow(String processId);
}
