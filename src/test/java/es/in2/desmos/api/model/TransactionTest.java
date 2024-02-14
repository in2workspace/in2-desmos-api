//package es.in2.desmos.api.model;
//
//import org.junit.jupiter.api.Test;
//
//import java.sql.Timestamp;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//class TransactionTest {
//
//    @Test
//    void testLombokGeneratedMethods() {
//        // Arrange
//        UUID id = UUID.randomUUID();
//        String transactionId = "trans123";
//        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
//        String dataLocation = "location1";
//        String entityId = "entity1";
//        String entityType = "type1";
//        String entityHash = "0x98765432";
//        TransactionStatus status = TransactionStatus.CREATED;
//        TransactionTrader trader = TransactionTrader.PRODUCER;
//        String hash = "0x12345678";
//        // Act
//        Transaction transaction = Transaction.builder().build();
//        transaction.setId(id);
//        transaction.setTransactionId(transactionId);
//        transaction.setCreatedAt(createdAt);
//        transaction.setDataLocation(dataLocation);
//        transaction.setEntityId(entityId);
//        transaction.setEntityType(entityType);
//        transaction.setEntityHash(entityHash);
//        transaction.setStatus(status);
//        transaction.setTrader(trader);
//        transaction.setHash(hash);
//        // Assert
//        assertEquals(id, transaction.getId());
//        assertEquals(transactionId, transaction.getTransactionId());
//        assertEquals(createdAt, transaction.getCreatedAt());
//        assertEquals(dataLocation, transaction.getDataLocation());
//        assertEquals(entityId, transaction.getEntityId());
//        assertEquals(entityType, transaction.getEntityType());
//        assertEquals(entityHash, transaction.getEntityHash());
//        assertEquals(status, transaction.getStatus());
//        assertEquals(trader, transaction.getTrader());
//        assertEquals(hash, transaction.getHash());
//    }
//
//    @Test
//    void testBuilderPattern() {
//        // Arrange
//        UUID id = UUID.randomUUID();
//        Transaction transaction = Transaction.builder()
//                .id(id)
//                .transactionId("trans123")
//                .createdAt(new Timestamp(System.currentTimeMillis()))
//                .dataLocation("location1")
//                .entityId("entity1")
//                .entityType("type1")
//                .entityHash("hash1")
//                .status(TransactionStatus.CREATED)
//                .trader(TransactionTrader.PRODUCER)
//                .hash("hash2")
//                .build();
//        // Assert
//        assertNotNull(transaction);
//        assertEquals(id, transaction.getId());
//    }
//
//    @Test
//    void testToString() {
//        // Arrange
//        UUID id = UUID.randomUUID();
//        String transactionId = "trans123";
//        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
//        String dataLocation = "data/location";
//        String entityId = "entity123";
//        String entityType = "typeA";
//        String entityHash = "hash123";
//        TransactionStatus status = TransactionStatus.CREATED;
//        TransactionTrader trader = TransactionTrader.PRODUCER;
//        String hash = "hashXYZ";
//
//        String expectedToString = "Transaction(id=" + id
//                + ", transactionId=" + transactionId
//                + ", createdAt=" + createdAt
//                + ", dataLocation=" + dataLocation
//                + ", entityId=" + entityId
//                + ", entityType=" + entityType
//                + ", entityHash=" + entityHash
//                + ", status=" + status
//                + ", trader=" + trader
//                + ", hash=" + hash + ")";
//        // Act
//        Transaction transaction = Transaction.builder()
//                .id(id)
//                .transactionId(transactionId)
//                .createdAt(createdAt)
//                .dataLocation(dataLocation)
//                .entityId(entityId)
//                .entityType(entityType)
//                .entityHash(entityHash)
//                .status(status)
//                .trader(trader)
//                .hash(hash)
//                .build();
//        // Assert
//        assertEquals(expectedToString, transaction.toString());
//    }
//
//}
//
