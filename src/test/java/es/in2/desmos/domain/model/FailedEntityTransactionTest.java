package es.in2.desmos.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.UUID;

class FailedEntityTransactionTest {

    @Test
    void testFailedEntityTransactionIsNew() {
        UUID id = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long notificationId = 123L;

        FailedEntityTransaction transaction = FailedEntityTransaction.builder()
                .id(id)
                .transactionId("txn123")
                .createdAt(now)
                .notificationId(notificationId)
                .entityId("entity123")
                .datalocation("http://example.com/data")
                .entityType("EntityType")
                .previousEntityHash("hash123")
                .entity("{json: 'value'}")
                .timestamp(System.currentTimeMillis())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .newTransaction(true)
                .build();

        assertTrue(transaction.isNew());
    }

    @Test
    void testFieldAssignmentAndRetrieval() {
        UUID id = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long notificationId = 123L;
        long timestamp = System.currentTimeMillis();

        FailedEntityTransaction transaction = FailedEntityTransaction.builder()
                .id(id)
                .transactionId("txn123")
                .createdAt(now)
                .notificationId(notificationId)
                .entityId("entity123")
                .datalocation("http://example.com/data")
                .entityType("EntityType")
                .previousEntityHash("hash123")
                .entity("{json: 'value'}")
                .timestamp(timestamp)
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .newTransaction(false)
                .build();

        assertEquals(id, transaction.getId());
        assertEquals("txn123", transaction.getTransactionId());
        assertEquals(now, transaction.getCreatedAt());
        assertEquals(notificationId, transaction.getNotificationId());
        assertEquals("entity123", transaction.getEntityId());
        assertEquals("http://example.com/data", transaction.getDatalocation());
        assertEquals("EntityType", transaction.getEntityType());
        assertEquals("hash123", transaction.getPreviousEntityHash());
        assertEquals("{json: 'value'}", transaction.getEntity());
        assertEquals(timestamp, transaction.getTimestamp());
        assertEquals(EventQueuePriority.PUBLICATION_PUBLISH, transaction.getPriority());
        assertFalse(transaction.isNew());
    }
}

