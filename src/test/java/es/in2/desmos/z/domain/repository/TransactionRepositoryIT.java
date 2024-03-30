//package es.in2.desmos.todo.domain.repository;
//
//import es.in2.desmos.ContainerManager;
//import es.in2.desmos.domain.models.Transaction;
//import es.in2.desmos.domain.models.TransactionStatus;
//import es.in2.desmos.domain.models.TransactionTrader;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//@SpringBootTest
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class TransactionRepositoryIT {
//
//    @DynamicPropertySource
//    static void setDynamicProperties(DynamicPropertyRegistry registry) {
//        ContainerManager.postgresqlProperties(registry);
//    }
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    private final Transaction transaction = Transaction.builder()
//            .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f5")
//            .createdAt(Timestamp.from(Instant.now()))
//            .datalocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:1234")
//            .entityId("urn:ngsi-ld:Entity:1234")
//            .entityType("Entity")
//            .entityHash("0x1234")
//            .status(TransactionStatus.CREATED)
//            .trader(TransactionTrader.PRODUCER)
//            .entityHash("0x9876")
//            .newTransaction(true)
//            .build();
//
//    @Order(1)
//    @Test
//    void shouldSaveTransaction() {
//        // Act
//        Transaction transactionMono = transactionRepository.save(transaction).block();
//        // Assert
//        assert transactionMono != null;
//        assertEquals(transaction.getTransactionId(), transactionMono.getTransactionId());
//    }
//
//    @Order(2)
//    @Test
//    void shouldRetrieveAllTransactions() {
//        Flux<Transaction> transactionFlux = transactionRepository.findAll();
//        StepVerifier.create(transactionFlux)
//                .assertNext(transaction1 -> assertEquals(transaction.getTransactionId(), transaction1.getTransactionId()))
//                .verifyComplete();
//    }
//
//    @Order(3)
//    @Test
//    void shouldFindTransactionsByEntityId() {
//        Flux<Transaction> transactionMono = transactionRepository.findByEntityId("urn:ngsi-ld:Entity:1234");
//        StepVerifier.create(transactionMono)
//                .assertNext(transaction1 ->
//                        assertEquals(transaction.getTransactionId(), transaction1.getTransactionId()))
//                .verifyComplete();
//    }
//
//    @Order(4)
//    @Test
//    void shouldFindLatestPublishedTransactionByEntityId() {
//        transaction.setStatus(TransactionStatus.PUBLISHED);
//        transactionRepository.save(transaction).block();
//        Transaction transactionMono = transactionRepository.findLatestByEntityIdAndStatusPublishedOrDeleted("urn:ngsi-ld:Entity:1234").block();
//        assert transactionMono != null;
//        assertEquals(transaction.getTransactionId(), transactionMono.getTransactionId());
//    }
//
//    @Order(5)
//    @Test
//    void shouldFindLatestDeletedTransactionByEntityId() {
//        transaction.setStatus(TransactionStatus.DELETED);
//        transactionRepository.save(transaction).block();
//        Transaction transactionMono = transactionRepository.findLatestByEntityIdAndStatusPublishedOrDeleted("urn:ngsi-ld:Entity:1234").block();
//        assert transactionMono != null;
//        assertEquals(transaction.getTransactionId(), transactionMono.getTransactionId());
//        // In database, we have a transaction which status is DELETED and PUBLISHED, the last one is DELETED (@Order(5))
//        assertEquals(TransactionStatus.DELETED, transactionMono.getStatus());
//    }
//
//    @Order(6)
//    @Test
//    void shouldVerifyLatestTransactionIsNotPublished() {
//        Transaction transactionMono = transactionRepository.findLatestByEntityIdAndStatusPublishedOrDeleted("urn:ngsi-ld:Entity:1234").block();
//        assert transactionMono != null;
//        // In database, we have a transaction which status is DELETED and PUBLISHED, the last one is DELETED (@Order(5))
//        assertNotEquals(TransactionStatus.PUBLISHED, transactionMono.getStatus());
//        assertEquals(TransactionStatus.DELETED, transactionMono.getStatus());
//    }
//
//    @Order(7)
//    @Test
//    void shouldDeleteAllTransactions() {
//        Mono<Void> transactionMono = transactionRepository.deleteAll();
//        StepVerifier.create(transactionMono)
//                .verifyComplete();
//    }
//
//}
