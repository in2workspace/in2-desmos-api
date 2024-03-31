package es.in2.desmos.domain.models;

import es.in2.desmos.domain.models.AuditRecordTrader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditRecordTraderTests {

    @Test
    void testEnumValues() {
        Assertions.assertEquals("producer", AuditRecordTrader.PRODUCER.getDescription());
        assertEquals("consumer", AuditRecordTrader.CONSUMER.getDescription());
    }

    @Test
    void testEnumCount() {
        assertEquals(2, AuditRecordTrader.values().length);
    }

}
