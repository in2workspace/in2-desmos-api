package es.in2.desmos.api.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testNoArgsConstructor() {
        // Act
        Transaction transaction = new Transaction();

        // Assert
        assertNull(transaction.getId(), "Expected id to be null");
        assertNull(transaction.getTransactionId(), "Expected transactionId to be null");
        assertNull(transaction.getCreatedAt(), "Expected createdAt to be null");
        assertNull(transaction.getDataLocation(), "Expected dataLocation to be null");
        assertNull(transaction.getEntityId(), "Expected entityId to be null");
        assertNull(transaction.getEntityType(), "Expected entityType to be null");
        assertNull(transaction.getEntityHash(), "Expected entityHash to be null");
        assertNull(transaction.getStatus(), "Expected status to be null");
        assertNull(transaction.getTrader(), "Expected trader to be null");
        assertNull(transaction.getHash(), "Expected hash to be null");
        assertTrue(transaction.isNew(), "Expected newTransaction to be false");
    }


    @Test
    void testLombokGeneratedMethods() {
        // Arrange
        UUID id = UUID.randomUUID();
        String transactionId = "trans123";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        String dataLocation = "location1";
        String entityId = "entity1";
        String entityType = "type1";
        String entityHash = "0x98765432";
        TransactionStatus status = TransactionStatus.CREATED;
        TransactionTrader trader = TransactionTrader.PRODUCER;
        String hash = "0x12345678";
        boolean newTransaction = true;
        // Act
        Transaction transaction = Transaction.builder().build();
        transaction.setId(id);
        transaction.setTransactionId(transactionId);
        transaction.setCreatedAt(createdAt);
        transaction.setDataLocation(dataLocation);
        transaction.setEntityId(entityId);
        transaction.setEntityType(entityType);
        transaction.setEntityHash(entityHash);
        transaction.setStatus(status);
        transaction.setTrader(trader);
        transaction.setHash(hash);
        transaction.setNewTransaction(newTransaction);
        // Assert
        assertEquals(id, transaction.getId());
        assertEquals(transactionId, transaction.getTransactionId());
        assertEquals(createdAt, transaction.getCreatedAt());
        assertEquals(dataLocation, transaction.getDataLocation());
        assertEquals(entityId, transaction.getEntityId());
        assertEquals(entityType, transaction.getEntityType());
        assertEquals(entityHash, transaction.getEntityHash());
        assertEquals(status, transaction.getStatus());
        assertEquals(trader, transaction.getTrader());
        assertEquals(hash, transaction.getHash());
        assertEquals(newTransaction, transaction.isNew());
    }

    @Test
    void testBuilderPattern() {
        // Arrange
        UUID id = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .id(id)
                .transactionId("trans123")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .dataLocation("location1")
                .entityId("entity1")
                .entityType("type1")
                .entityHash("hash1")
                .status(TransactionStatus.CREATED)
                .trader(TransactionTrader.PRODUCER)
                .hash("hash2")
                .build();
        // Assert
        assertNotNull(transaction);
        assertEquals(id, transaction.getId());
    }

    @Test
    void testToString() {
        // Arrange
        UUID id = UUID.randomUUID();
        String transactionId = "trans123";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        String dataLocation = "data/location";
        String entityId = "entity123";
        String entityType = "typeA";
        String entityHash = "hash123";
        TransactionStatus status = TransactionStatus.CREATED;
        TransactionTrader trader = TransactionTrader.PRODUCER;
        String hash = "hashXYZ";
        boolean newTransaction = true;

        String expectedToString = "Transaction(id=" + id
                + ", transactionId=" + transactionId
                + ", createdAt=" + createdAt
                + ", dataLocation=" + dataLocation
                + ", entityId=" + entityId
                + ", entityType=" + entityType
                + ", entityHash=" + entityHash
                + ", status=" + status
                + ", trader=" + trader
                + ", hash=" + hash
                + ", newTransaction=" + newTransaction + ")";
        // Act
        Transaction transaction = Transaction.builder()
                .id(id)
                .transactionId(transactionId)
                .createdAt(createdAt)
                .dataLocation(dataLocation)
                .entityId(entityId)
                .entityType(entityType)
                .entityHash(entityHash)
                .status(status)
                .trader(trader)
                .hash(hash)
                .newTransaction(newTransaction)
                .build();
        // Assert
        assertEquals(expectedToString, transaction.toString());
    }

    @Test
    void testIsNew() {
        // Arrange
        UUID id = UUID.randomUUID();
        String transactionId = "trans123";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        String dataLocation = "data/location";
        String entityId = "entity123";
        String entityType = "typeA";
        String entityHash = "hash123";
        TransactionStatus status = TransactionStatus.CREATED;
        TransactionTrader trader = TransactionTrader.PRODUCER;
        String hash = "hashXYZ";

        // Act
        Transaction transaction = Transaction.builder()
                .id(id)
                .transactionId(transactionId)
                .createdAt(createdAt)
                .dataLocation(dataLocation)
                .entityId(entityId)
                .entityType(entityType)
                .entityHash(entityHash)
                .status(status)
                .trader(trader)
                .hash(hash)
                .build();

        // Assert
        assertFalse(transaction.isNew(), "Expected the transaction to be not new");

        // Act
        transaction = Transaction.builder()
                .transactionId(transactionId)
                .createdAt(createdAt)
                .dataLocation(dataLocation)
                .entityId(entityId)
                .entityType(entityType)
                .entityHash(entityHash)
                .status(status)
                .trader(trader)
                .hash(hash)
                .build();

        // Assert
        assertTrue(transaction.isNew(), "Expected the transaction to be new");

        // Act
        transaction = Transaction.builder()
                .id(id)
                .transactionId(transactionId)
                .createdAt(createdAt)
                .dataLocation(dataLocation)
                .entityId(entityId)
                .entityType(entityType)
                .entityHash(entityHash)
                .status(status)
                .trader(trader)
                .hash(hash)
                .newTransaction(true)
                .build();

        // Assert
        assertTrue(transaction.isNew(), "Expected the transaction to be new because newTransaction is set to true");
    }

    @Test
    void testTransactionBuilderToString() {
        // Arrange
        UUID id = UUID.randomUUID();
        String transactionId = "trans123";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        String dataLocation = "data/location";
        String entityId = "entity123";
        String entityType = "typeA";
        String entityHash = "hash123";
        TransactionStatus status = TransactionStatus.CREATED;
        TransactionTrader trader = TransactionTrader.PRODUCER;
        String hash = "hashXYZ";
        boolean newTransaction = true;

        String expectedToString = "Transaction.TransactionBuilder(id=" + id
                + ", transactionId=" + transactionId
                + ", createdAt=" + createdAt
                + ", dataLocation=" + dataLocation
                + ", entityId=" + entityId
                + ", entityType=" + entityType
                + ", entityHash=" + entityHash
                + ", status=" + status
                + ", trader=" + trader
                + ", hash=" + hash
                + ", newTransaction=" + newTransaction + ")";


        // Act
        Transaction.TransactionBuilder transactionBuilder = Transaction.builder()
                .id(id)
                .transactionId(transactionId)
                .createdAt(createdAt)
                .dataLocation(dataLocation)
                .entityId(entityId)
                .entityType(entityType)
                .entityHash(entityHash)
                .status(status)
                .trader(trader)
                .hash(hash)
                .newTransaction(newTransaction);

        // Assert
        assertEquals(expectedToString, transactionBuilder.toString());
    }


}