package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditRecordTraderTests {

    @Test
    void testEnumValues() {
        assertEquals("producer", AuditRecordTrader.PRODUCER.getDescription());
        assertEquals("consumer", AuditRecordTrader.CONSUMER.getDescription());
    }

    @Test
    void testEnumCount() {
        assertEquals(2, AuditRecordTrader.values().length);
    }

}