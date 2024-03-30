package es.in2.desmos.domain.config;

import es.in2.desmos.domain.model.EventQueue;
import es.in2.desmos.domain.model.EventQueuePriority;
import es.in2.desmos.domain.model.FailedEntityTransaction;
import es.in2.desmos.schedulers.DataRecoveryScheduler;
import es.in2.desmos.domain.service.QueueService;
import es.in2.desmos.z.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataRecoverySchedulerTest {

    @Mock
    private QueueService dataPublicationQueue;

    @Mock
    private QueueService dataRetrievalQueue;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private DataRecoveryScheduler dataRecoveryScheduler;

    @Test
    void testProcessFailedEvents() {
        when(transactionService.getAllFailedEventTransactions(anyString()))
                .thenReturn(Flux.empty());

        dataRecoveryScheduler.processFailedEvents();

        verify(transactionService, times(1)).getAllFailedEventTransactions(anyString());
        verify(transactionService, never()).deleteFailedEventTransaction(anyString(), any());
        verify(dataPublicationQueue, never()).enqueueEvent(any(EventQueue.class));
    }

    @Test
    void testProcessFailedEntities() {
        when(transactionService.getAllFailedEntityTransactions(anyString()))
                .thenReturn(Flux.empty());

        dataRecoveryScheduler.processFailedEntities();

        verify(transactionService, times(1)).getAllFailedEntityTransactions(anyString());
        verify(transactionService, never()).deleteFailedEntityTransaction(anyString(), any());
        verify(dataRetrievalQueue, never()).enqueueEvent(any(EventQueue.class));
    }

    @Test
    void testProcessFailedEntitiesWithFoundEntities() {
        when(transactionService.getAllFailedEntityTransactions(anyString()))
                .thenReturn(Flux.just(createFailedEntityTransaction()));
        when(transactionService.deleteFailedEntityTransaction(anyString(), any()))
                .thenReturn(Mono.empty());

        dataRecoveryScheduler.processFailedEntities();

        verify(transactionService).deleteFailedEntityTransaction(anyString(), any());
    }
    private FailedEntityTransaction createFailedEntityTransaction() {
        return FailedEntityTransaction.builder()
                .id(UUID.randomUUID())
                .notificationId(12345)
                .datalocation("Test Data Location")
                .timestamp(542315)
                .entityType("Test Entity Type")
                .entityId("Test Entity ID")
                .previousEntityHash("Test Previous Entity Hash")
                .entity("Test Entity")
                .priority(EventQueuePriority.RECOVER_PUBLISH)
                .build();
    }



}

