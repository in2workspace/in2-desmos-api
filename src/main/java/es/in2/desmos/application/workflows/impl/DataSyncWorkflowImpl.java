package es.in2.desmos.application.workflows.impl;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import es.in2.desmos.application.workflows.jobs.P2PDataSyncJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncWorkflowImpl implements DataSyncWorkflow {

    private final P2PDataSyncJob p2PDataSyncJob;

    @Override
    public Flux<Void> startDataSyncWorkflow(String processId) {
        return p2PDataSyncJob.synchronizeData(processId).flux();
    }

}
