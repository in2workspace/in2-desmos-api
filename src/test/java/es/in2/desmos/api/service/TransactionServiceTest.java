package es.in2.desmos.api.service;

import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.repository.TransactionRepository;
import es.in2.desmos.api.service.impl.TransactionServiceImpl;
import es.in2.desmos.broker.config.properties.BrokerPathProperties;
import es.in2.desmos.broker.config.properties.BrokerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private final Transaction transactionSample = Transaction.builder()
            .id(UUID.randomUUID())
            .transactionId("sampleTransactionId")
            .createdAt(Timestamp.from(Instant.now()))
            .datalocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:in2-0092?hl" +
                    "=0502b524143e41325d7d60aa1e5c19ab1597f1af9f2acd4d4101d643a9494d2b")
            .entityId("sampleEntityId")
            .entityHash("9520d52c750e7ee0678b67849f075958346811c866bcf46be4a5da166ac95470")
            .status(TransactionStatus.CREATED)
            .trader(TransactionTrader.PRODUCER)
            .hash("sampleHash")
            .build();

    private final Transaction transactionSample_deleted = Transaction.builder()
            .id(UUID.randomUUID())
            .transactionId("sampleTransactionId")
            .createdAt(Timestamp.from(Instant.now()))
            .datalocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:in2-0092")
            .entityId("sampleEntityId")
            .entityHash("")
            .status(TransactionStatus.DELETED)
            .trader(TransactionTrader.PRODUCER)
            .build();

    private final Transaction consumerTransactionSample = Transaction.builder()
            .id(UUID.randomUUID())
            .transactionId("sampleTransactionId")
            .createdAt(Timestamp.from(Instant.now()))
            .datalocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:in2-0092")
            .entityId("sampleEntityId")
            .entityHash("")
            .status(TransactionStatus.DELETED)
            .trader(TransactionTrader.CONSUMER)
            .build();

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BrokerProperties brokerProperties;

    @Mock
    private BrokerPathProperties brokerPathProperties;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BrokerPathProperties brokerPathProperties = new BrokerPathProperties("/v2", "/entities", "/subscriptions");
        BrokerProperties brokerProperties = new BrokerProperties("scorpio", "http://localhost:1026",
                "http://localhost:1026", new BrokerPathProperties("/entities", "/subscriptions", "/v2"));
        transactionService = new TransactionServiceImpl(transactionRepository, brokerProperties);
    }

//    @Test
//    void saveTransaction_Success() {
//        // Arrange
//        String processId = "testProcessId";
//
//        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.empty());
//        when(transactionService.getLastProducerTransactionByEntityId(anyString(), anyString())).thenReturn(Mono.just(transactionSample));
//
//        // Act
//        Mono<Void> resultMono = transactionService.saveTransaction(processId, transactionSample);
//
//        // Assert
//        Void resultTransaction = resultMono.block();
//        assertNull(resultTransaction);
//
//        // Verify that save was called exactly once with any Transaction object as an argument
//        verify(transactionRepository, times(1)).save(any(Transaction.class));
//    }


    @Test
    void saveDeletedTransaction_Success() {
        // Arrange
        String processId = "testProcessId";
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.empty());
        // Act
        Mono<Void> resultMono = transactionService.saveTransaction(processId, transactionSample_deleted);
        // Assert
        Void resultTransaction = resultMono.block();
        assertNull(resultTransaction);
    }

    @Test
    void saveConsumerTransaction_Success() {
        // Arrange
        String processId = "testProcessId";
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.empty());
        // Act
        Mono<Void> resultMono = transactionService.saveTransaction(processId, consumerTransactionSample);
        // Assert
        Void resultTransaction = resultMono.block();
        assertNull(resultTransaction);
    }

//    @Test
//    void saveTransaction_Error() {
//        // Arrange
//        String processId = "testProcessId";
//        RuntimeException testException = new RuntimeException("Test Error");
//        when(transactionRepository.save(transactionSample)).thenReturn(Mono.error(testException));
//        when(transactionRepository.findLastProducerTransaction()).thenReturn(Flux.empty());
//
//        // Act
//        Mono<Void> resultMono = transactionService.saveTransaction(processId, transactionSample);
//        // Assert
//        StepVerifier.create(resultMono)
//                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Test Error"))
//                .verify();
//    }

    @Test
    void getLastProducerTransactionByEntityId_ReturnsLastTransaction() {
        String processId = "processId";
        String entityId = "entityId";
        Transaction transaction = new Transaction();
        when(transactionRepository.findLastProducerTransactionByEntityId(entityId)).thenReturn(Flux.just(transaction));

        StepVerifier.create(transactionService.getLastProducerTransactionByEntityId(processId, entityId))
                .expectNext(transaction)
                .verifyComplete();
        verify(transactionRepository).findLastProducerTransactionByEntityId(entityId);
    }

    @Test
    void getTransactionShouldReturnTransactions() {
        String processId = "testProcessId";
        String transactionId = "testId";
        List<Transaction> expectedTransactions = List.of(Transaction.builder().build(), Transaction.builder().build());
        when(transactionRepository.findByEntityId(transactionId)).thenReturn(Flux.fromIterable(expectedTransactions));
        Mono<List<Transaction>> result = transactionService.getTransactionsByEntityId(processId, transactionId);
        StepVerifier.create(result)
                .expectNextMatches(transactions -> transactions.equals(expectedTransactions))
                .verifyComplete();
        verify(transactionRepository).findByEntityId(transactionId);
    }

    @Test
    void getTransactionShouldHandleError() {
        String processId = "testProcessId";
        String transactionId = "testId";
        when(transactionRepository.findByEntityId(transactionId)).thenReturn(Flux.error(new RuntimeException("Error")));
        Mono<List<Transaction>> result = transactionService.getTransactionsByEntityId(processId, transactionId);
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Error"))
                .verify();
        verify(transactionRepository).findByEntityId(transactionId);
    }

    @Test
    void getAllTransactionsShouldReturnAllTransactions() {
        String processId = "testProcessId";
        List<Transaction> expectedTransactions = List.of(
                Transaction.builder().build(),
                Transaction.builder().build()
        );
        when(transactionRepository.findAll()).thenReturn(Flux.fromIterable(expectedTransactions));

        StepVerifier.create(transactionService.getAllTransactions(processId))
                .expectNextMatches(transactions -> transactions.equals(expectedTransactions.get(0)))
                .expectNextMatches(transactions -> transactions.equals(expectedTransactions.get(1)))
                .verifyComplete();

        verify(transactionRepository).findAll();
    }

    @Test
    void findLatestPublishedOrDeletedTransactionForEntityShouldReturnTransaction() {
        String processId = "testProcessId";
        String entityId = "sampleEntityId";
        Transaction expectedTransaction = Transaction.builder().build();
        when(transactionRepository.findLatestByEntityIdAndStatusPublishedOrDeleted(entityId)).thenReturn(Mono.just(expectedTransaction));

        StepVerifier.create(transactionService.findLatestPublishedOrDeletedTransactionForEntity(processId, entityId))
                .expectNextMatches(transaction -> transaction.equals(expectedTransaction))
                .verifyComplete();

        verify(transactionRepository).findLatestByEntityIdAndStatusPublishedOrDeleted(entityId);
    }

    @Test
    void findLatestPublishedOrDeletedTransactionForEntityShouldHandleError() {
        String processId = "testProcessId";
        String entityId = "sampleEntityId";
        when(transactionRepository.findLatestByEntityIdAndStatusPublishedOrDeleted(entityId)).thenReturn(Mono.error(new RuntimeException(
                "Error")));

        StepVerifier.create(transactionService.findLatestPublishedOrDeletedTransactionForEntity(processId, entityId))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Error"))
                .verify();

        verify(transactionRepository).findLatestByEntityIdAndStatusPublishedOrDeleted(entityId);
    }


}