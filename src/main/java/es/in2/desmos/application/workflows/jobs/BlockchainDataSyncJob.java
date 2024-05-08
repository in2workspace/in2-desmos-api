package es.in2.desmos.application.workflows.jobs;

import reactor.core.publisher.Flux;

public interface BlockchainDataSyncJob {
    Flux<Void> startBlockchainDataSyncJob(String processId);
}
