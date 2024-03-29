package es.in2.desmos.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTraderTest {

    @Test
    void testEnumValues() {
        assertEquals("producer", TransactionTrader.PRODUCER.getDescription());
        assertEquals("consumer", TransactionTrader.CONSUMER.getDescription());
    }

    @Test
    void testEnumCount() {
        assertEquals(2, TransactionTrader.values().length);
    }

}
