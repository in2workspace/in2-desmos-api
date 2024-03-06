package es.in2.desmos.api.config;

import es.in2.desmos.api.model.EventQueue;
import es.in2.desmos.api.model.EventQueuePriority;
import es.in2.desmos.api.model.FailedEntityTransaction;
import es.in2.desmos.api.model.FailedEventTransaction;
import es.in2.desmos.api.service.QueueService;
import es.in2.desmos.api.service.TransactionService;
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
class FailedTransactionSchedulerTest {

    @Mock
    private QueueService brokerToBlockchainQueueService;

    @Mock
    private QueueService blockchainToBrokerQueueService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private FailedTransactionScheduler failedTransactionScheduler;

    @Test
    void testProcessFailedEvents() {
        when(transactionService.getAllFailedEventTransactions(anyString()))
                .thenReturn(Flux.empty());

        failedTransactionScheduler.processFailedEvents();

        verify(transactionService, times(1)).getAllFailedEventTransactions(anyString());
        verify(transactionService, never()).deleteFailedEventTransaction(anyString(), any());
        verify(brokerToBlockchainQueueService, never()).enqueueEvent(any(EventQueue.class));
    }

    @Test
    void testProcessFailedEntities() {
        when(transactionService.getAllFailedEntityTransactions(anyString()))
                .thenReturn(Flux.empty()); // Simula no encontrar entidades fallidas

        failedTransactionScheduler.processFailedEntities();

        verify(transactionService, times(1)).getAllFailedEntityTransactions(anyString());
        verify(transactionService, never()).deleteFailedEntityTransaction(anyString(), any());
        verify(blockchainToBrokerQueueService, never()).enqueueEvent(any(EventQueue.class));
    }

    @Test
    void testProcessFailedEventsWithFoundEvents() {
        when(transactionService.getAllFailedEventTransactions(anyString()))
                .thenReturn(Flux.just(createFailedEventTransaction()));
        when(transactionService.deleteFailedEventTransaction(anyString(), any()))
                .thenReturn(Mono.empty());
        when(brokerToBlockchainQueueService.enqueueEvent(any(EventQueue.class)))
                .thenReturn(Mono.empty());

        failedTransactionScheduler.processFailedEvents();

        verify(transactionService).deleteFailedEventTransaction(anyString(), any());
        verify(brokerToBlockchainQueueService).enqueueEvent(any(EventQueue.class));
    }

    @Test
    void testProcessFailedEntitiesWithFoundEntities() {
        when(transactionService.getAllFailedEntityTransactions(anyString()))
                .thenReturn(Flux.just(createFailedEntityTransaction()));
        when(transactionService.deleteFailedEntityTransaction(anyString(), any()))
                .thenReturn(Mono.empty());

        failedTransactionScheduler.processFailedEntities();

        verify(transactionService).deleteFailedEntityTransaction(anyString(), any());
    }

    private FailedEventTransaction createFailedEventTransaction() {
        FailedEventTransaction failedEventTransaction = new FailedEventTransaction();
        failedEventTransaction.setId(UUID.randomUUID());
        failedEventTransaction.setEntityType("Test Entity");
        failedEventTransaction.setDatalocation("Test Data Location");
        failedEventTransaction.setEntityId("Test Entity ID");
        failedEventTransaction.setPreviousEntityHash("Test Previous Entity Hash");
        failedEventTransaction.setOrganizationId("Test Organization ID");
        failedEventTransaction.setPriority(EventQueuePriority.RECOVEREDIT);
        return failedEventTransaction;
    }

private FailedEntityTransaction createFailedEntityTransaction() {
    FailedEntityTransaction failedEntityTransaction = new FailedEntityTransaction();
    failedEntityTransaction.setId(UUID.randomUUID());
    failedEntityTransaction.setNotificationId(12345);
    failedEntityTransaction.setDatalocation("Test Data Location");
    failedEntityTransaction.setTimestamp(542315);
    failedEntityTransaction.setEntityType("Test Entity Type");
    failedEntityTransaction.setEntityId("Test Entity ID");
    failedEntityTransaction.setPreviousEntityHash("Test Previous Entity Hash");
    failedEntityTransaction.setEntity("Test Entity");
    failedEntityTransaction.setPriority(EventQueuePriority.RECOVERPUBLISH);
    return failedEntityTransaction;
}



}

