package es.in2.desmos.api.config;

import es.in2.desmos.api.model.EventQueue;
import es.in2.desmos.api.service.QueueService;
import es.in2.desmos.api.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
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
}

