package es.in2.desmos.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.UUID;

class FailedEventTransactionTest {

    @Test
    void testFailedEventTransactionIsNew() {
        UUID id = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        FailedEventTransaction transactionWithId = new FailedEventTransaction(id, "tx123", now, "entity123", "http://example.com", "ExampleType", "org123", "0xhash", EventQueuePriority.PUBLICATION_PUBLISH, false);
        assertFalse(transactionWithId.isNew());

        FailedEventTransaction transactionWithoutId = new FailedEventTransaction();
        transactionWithoutId.setId(null);
        transactionWithoutId.setNewTransaction(true);
        assertTrue(transactionWithoutId.isNew());

        FailedEventTransaction transactionMarkedAsNew = new FailedEventTransaction();
        transactionMarkedAsNew.setId(id);
        transactionMarkedAsNew.setNewTransaction(true);
        assertTrue(transactionMarkedAsNew.isNew());
    }

    @Test
    void testFieldAssignmentAndRetrieval() {
        UUID id = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        FailedEventTransaction transaction = FailedEventTransaction.builder()
                .id(id)
                .transactionId("tx123")
                .createdAt(now)
                .entityId("entity123")
                .datalocation("http://example.com")
                .entityType("ExampleType")
                .organizationId("org123")
                .previousEntityHash("0xhash")
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .newTransaction(true)
                .build();

        assertEquals(id, transaction.getId());
        assertEquals("tx123", transaction.getTransactionId());
        assertEquals(now, transaction.getCreatedAt());
        assertEquals("entity123", transaction.getEntityId());
        assertEquals("http://example.com", transaction.getDatalocation());
        assertEquals("ExampleType", transaction.getEntityType());
        assertEquals("org123", transaction.getOrganizationId());
        assertEquals("0xhash", transaction.getPreviousEntityHash());
        assertEquals(EventQueuePriority.PUBLICATION_PUBLISH, transaction.getPriority());
        assertTrue(transaction.isNew());
    }
}

