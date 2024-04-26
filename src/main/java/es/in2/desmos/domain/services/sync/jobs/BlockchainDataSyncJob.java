package es.in2.desmos.domain.services.sync.jobs;

import reactor.core.publisher.Flux;

public interface BlockchainDataSyncJob {
    Flux<Void> startBlockchainDataSyncJob(String processId);
}
