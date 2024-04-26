package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.services.sync.jobs.BlockchainDataSyncJob;
import es.in2.desmos.workflows.DataSyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncWorkflowImpl implements DataSyncWorkflow {

    private final BlockchainDataSyncJob blockchainDataSyncJob;

    @Override
    public Flux<Void> startDataSyncWorkflow(String processId) {
        // TODO: Change blockchainDataSyncService to p2pDataSyncService in future
        return blockchainDataSyncJob.startBlockchainDataSyncJob(processId);
    }

}
