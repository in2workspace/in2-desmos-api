package es.in2.desmos.application.schedulers;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSyncSchedulerTests {
    @InjectMocks
    DataSyncScheduler dataSyncScheduler;

    @Mock
    DataSyncWorkflow dataSyncWorkflow;

    @Test
    void itShouldCallStartDataSyncWorkflow() {
        when(dataSyncWorkflow.startDataSyncWorkflow(anyString())).thenReturn(Flux.empty());

        Flux<Void> result = dataSyncScheduler.initializeDataSync();

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(dataSyncWorkflow, times(1)).startDataSyncWorkflow(any());
        verifyNoMoreInteractions(dataSyncWorkflow);
    }
}