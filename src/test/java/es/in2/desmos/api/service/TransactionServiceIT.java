package es.in2.desmos.api.service;

import es.in2.desmos.ContainerManager;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("it")
@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionServiceIT {

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Autowired
    private TransactionServiceImpl transactionService;

    private final Transaction transactionSample = Transaction.builder()
            .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f5")
            .createdAt(Timestamp.from(Instant.now()))
            .hashlink("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:1234?hl=0502b524143e41325d7d60aa1e5c19ab1597f1af9f2acd4d4101d643a9494d2b")
            .entityId("urn:ngsi-ld:Entity:1234")
            .entityType("Entity")
            .hash("0x1234")
            .status(TransactionStatus.CREATED)
            .trader(TransactionTrader.PRODUCER)
            .newTransaction(true)
            .build();

    @Order(1)
    @Test
    void saveTransaction_Success() {
        // Arrange
        String processId = "1234";
        // Act
        Mono<Void> resultMono = transactionService.saveTransaction(processId, transactionSample);
        // Assert
        Void resultTransaction = resultMono.block();
        assertNull(resultTransaction);
    }

    @Order(2)
    @Test
    void getTransactionShouldReturnTransactions() {
        // Arrange
        String processId = "1234";
        String entityId = "urn:ngsi-ld:Entity:1234";
        List<Transaction> expectedTransactionList = List.of(transactionSample);
        // Act
        List<Transaction> transactionList = transactionService.getTransactionsByEntityId(processId, entityId).block();
        // Assert
        assert transactionList != null;
        assertEquals(expectedTransactionList.size(), transactionList.size());
        assertEquals(expectedTransactionList.get(0).getTransactionId(), transactionSample.getTransactionId());
    }

    @Order(3)
    @Test
    void getAllTransactionsShouldReturnAllTransactions() {
        // Arrange
        String processId = "testProcessId";
        transactionService.saveTransaction("9876", Transaction.builder()
                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f6")
                .createdAt(Timestamp.from(Instant.now()))
                .hashlink("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:1235?hl=0502b524143e41325d7d60aa1e5c19ab1597f1af9f2acd4d4101d643a9494d2b")
                .entityId("urn:ngsi-ld:Entity:1235")
                .entityType("Entity")
                .hash("0x1234")
                .status(TransactionStatus.PUBLISHED)
                .trader(TransactionTrader.PRODUCER)
                .hash("0x9876")
                        .newTransaction(true)
                .build()).block();
        // Act
        Flux<Transaction> resultFlux = transactionService.getAllTransactions(processId);
        // Assert
        StepVerifier.create(resultFlux)
                .assertNext(transaction1 -> assertEquals("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f5", transaction1.getTransactionId()))
                .assertNext(transaction1 -> assertEquals("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f6", transaction1.getTransactionId()))
                .verifyComplete();
    }

    @Order(4)
    @Test
    void findLatestPublishedOrDeletedTransactionForEntityShouldReturnTransaction() {
        // Arrange
        String processId = "1234";
        String entityId = "urn:ngsi-ld:Entity:1235";
        // Act
        Mono<Transaction> transactionMono = transactionService.findLatestPublishedOrDeletedTransactionForEntity(processId, entityId);
        // Assert
        StepVerifier.create(transactionMono)
                .assertNext(transaction1 -> assertEquals("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f6", transaction1.getTransactionId()))
                .verifyComplete();
    }

}
