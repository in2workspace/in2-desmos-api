package es.in2.desmos.application.workflows.impl;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import es.in2.desmos.domain.services.sync.jobs.P2PDataSyncJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncWorkflowImpl implements DataSyncWorkflow {

    @SuppressWarnings({"GrazieInspection", "java:S125"})
    // private final BlockchainDataSyncJob blockchainDataSyncJob;

    private final P2PDataSyncJob p2PDataSyncJob;

    @Override
    public Flux<Void> startDataSyncWorkflow(String processId) {
        // TODO: We have not yet decided which job will be applied, so for the moment we will keep both options in the code

        // return blockchainDataSyncJob.startBlockchainDataSyncJob(processId); // NOSONAR

        return p2PDataSyncJob.synchronizeData(processId).flux();
    }
}
