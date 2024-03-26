package es.in2.desmos.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.api.model.*;
import es.in2.desmos.api.repository.FailedEntityTransactionRepository;
import es.in2.desmos.api.repository.FailedEventTransactionRepository;
import es.in2.desmos.api.repository.TransactionRepository;
import es.in2.desmos.api.service.impl.TransactionServiceImpl;
import es.in2.desmos.api.util.ApplicationUtils;
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

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static es.in2.desmos.api.util.ApplicationUtils.calculateIntertwinedHash;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
            .hash("0502b524143e41325d7d60b67849f075958346811c866bcf46be4a5da166ac95")
            .build();

    private final Transaction newTransactionSample = Transaction.builder()
            .id(UUID.randomUUID())
            .transactionId("sampleTransactionId")
            .createdAt(Timestamp.from(Instant.now()))
            .datalocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:in2-0092?hl" +
                    "=0502b524143e41325d7d60aa1e5c19ab1597f1af9f2acd4d4101d643a9494d2b")
            .entityId("sampleEntityId")
            .entityHash("9520d52c750e7ee0678b67849f075958346811c866bcf46be4a5da166ac95470")
            .status(TransactionStatus.CREATED)
            .trader(TransactionTrader.PRODUCER)
            .newTransaction(true)
            .entityType("ProductOffering")
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

    private final FailedEventTransaction failedEventTransaction = FailedEventTransaction.builder()
            .id(UUID.randomUUID())
            .transactionId("sampleTransactionId")
            .createdAt(Timestamp.from(Instant.now()))
            .datalocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:in2-0092")
            .entityId("sampleEntityId")
            .previousEntityHash("previousEntityHash")
            .priority(EventQueuePriority.SYNCHRONIZATION)
            .newTransaction(true)
            .build();

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FailedEventTransactionRepository failedTransactionRepository;

    @Mock
    private FailedEntityTransactionRepository failedEntityTransactionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BrokerPathProperties brokerPathProperties = new BrokerPathProperties("/v2", "/entities", "/subscriptions");
        BrokerProperties brokerProperties = new BrokerProperties("scorpio", "http://localhost:1026",
                "http://localhost:1026", new BrokerPathProperties("/entities", "/subscriptions", "/v2"));
        transactionService = new TransactionServiceImpl(transactionRepository, failedTransactionRepository, failedEntityTransactionRepository, objectMapper);
    }

    @Test
    void saveFailedEventTransactionTest() {
        // Arrange
        String processId = "process123";
        FailedEventTransaction failedEventTransaction = FailedEventTransaction.builder()
                .id(UUID.randomUUID())
                .transactionId("transaction123")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .entityId("entity123")
                .datalocation("http://example.com/data/location")
                .entityType("EntityType")
                .organizationId("org123")
                .previousEntityHash("hash123")
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .newTransaction(true)
                .build();

        when(failedTransactionRepository.save(any(FailedEventTransaction.class)))
                .thenReturn(Mono.just(failedEventTransaction));

        // Act & Assert
        StepVerifier.create(transactionService.saveFailedEventTransaction(processId, failedEventTransaction))
                .verifyComplete();

        verify(failedTransactionRepository).save(failedEventTransaction);
    }

    @Test
    void deleteFailedEntityTransactionTest() {
        // Arrange
        String processId = "process123";
        UUID transactionId = UUID.randomUUID();

        when(failedEntityTransactionRepository.deleteById(transactionId))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(transactionService.deleteFailedEntityTransaction(processId, transactionId))
                .verifyComplete();

        verify(failedEntityTransactionRepository).deleteById(transactionId);
    }

    @Test
    void deleteFailedEventTransactionTest() {
        // Arrange
        String processId = "process123";
        UUID transactionId = UUID.randomUUID();

        when(failedTransactionRepository.deleteById(transactionId))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(transactionService.deleteFailedEventTransaction(processId, transactionId))
                .verifyComplete();

        verify(failedTransactionRepository).deleteById(transactionId);
    }




    @Test
    void saveTransaction_Success() throws JsonProcessingException {
        // Arrange
        String processId = "testProcessId";

        when(transactionService.getPreviousTransaction(processId)).thenReturn(Mono.just(transactionSample));
        when(objectMapper.writeValueAsString(any())).thenReturn("Hashexample");
        when(transactionRepository.findLastTransactionByEntityId("sampleEntityId")).thenReturn(Flux.just(transactionSample));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> resultMono = transactionService.saveTransaction(processId, transactionSample)
                .doOnError(Throwable::printStackTrace);

        // Assert
        Void resultTransaction = resultMono.block();
        assertNull(resultTransaction);

        // Verify that save was called exactly once with any Transaction object as an argument
        verify(transactionRepository, times(3)).save(any(Transaction.class));

    }


    @Test
    void saveFailedEntityTransaction() {
        String processId = "processId";
        FailedEntityTransaction transaction = FailedEntityTransaction.builder().build();
        when(failedEntityTransactionRepository.save(transaction)).thenReturn(Mono.just(transaction));

        StepVerifier.create(transactionService.saveFailedEntityTransaction(processId, transaction))
                .verifyComplete();

        verify(failedEntityTransactionRepository).save(transaction);
    }

    @Test
    void saveFailedEntityTransactionWithError() {
        String processId = "processId";
        FailedEntityTransaction transaction = FailedEntityTransaction.builder().build();
        when(failedEntityTransactionRepository.save(transaction)).thenReturn(Mono.error(new RuntimeException("Simulated error")));

        StepVerifier.create(transactionService.saveFailedEntityTransaction(processId, transaction))
                .expectErrorMessage("Simulated error")
                .verify();

        verify(failedEntityTransactionRepository).save(transaction);
    }

    @Test
    void getLastProducerTransactionByEntityId_ReturnsLastTransaction() {
        String processId = "processId";
        String entityId = "entityId";
        Transaction transaction = Transaction.builder().build();
        when(transactionRepository.findLastTransactionByEntityId(entityId)).thenReturn(Flux.just(transaction));

        StepVerifier.create(transactionService.getLastProducerTransactionByEntityId(processId, entityId))
                .expectNext(transaction)
                .verifyComplete();
        verify(transactionRepository).findLastTransactionByEntityId(entityId);
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
