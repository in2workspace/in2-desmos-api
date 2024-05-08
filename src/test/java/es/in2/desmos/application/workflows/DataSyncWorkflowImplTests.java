package es.in2.desmos.application.workflows;

import es.in2.desmos.application.workflows.impl.DataSyncWorkflowImpl;
import es.in2.desmos.domain.services.sync.jobs.P2PDataSyncJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSyncWorkflowImplTests {
    @InjectMocks
    DataSyncWorkflowImpl dataSyncWorkflow;

    @Mock
    P2PDataSyncJob p2PDataSyncJob;

    @Test
    void itShouldCallStartDataSyncWorkflow() {
        String processId = "0";
        when(p2PDataSyncJob.synchronizeData(anyString())).thenReturn(Mono.empty());

        Flux<Void> result = dataSyncWorkflow.startDataSyncWorkflow(processId);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(p2PDataSyncJob, times(1)).synchronizeData(processId);
        verifyNoMoreInteractions(p2PDataSyncJob);
    }

}