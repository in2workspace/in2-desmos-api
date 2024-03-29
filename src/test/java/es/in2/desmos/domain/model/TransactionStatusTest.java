package es.in2.desmos.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionStatusTest {

    @Test
    void testEnumValues() {
        assertEquals("notified", TransactionStatus.RECEIVED.getDescription());
        assertEquals("created", TransactionStatus.CREATED.getDescription());
        assertEquals("retrieved", TransactionStatus.RETRIEVED.getDescription());
        assertEquals("published", TransactionStatus.PUBLISHED.getDescription());
        assertEquals("deleted", TransactionStatus.DELETED.getDescription());
    }

    @Test
    void testEnumCount() {
        assertEquals(5, TransactionStatus.values().length);
    }

}
