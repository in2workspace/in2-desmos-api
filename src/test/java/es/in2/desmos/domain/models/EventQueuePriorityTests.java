package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventQueuePriorityTests {

    @Test
    void testEnumValues() {
        assertEquals(1, EventQueuePriority.CRITICAL.getPriority());
        assertEquals(2, EventQueuePriority.HIGH.getPriority());
        assertEquals(3, EventQueuePriority.MEDIUM.getPriority());
        assertEquals(4, EventQueuePriority.LOW.getPriority());
        assertEquals(5, EventQueuePriority.MINOR.getPriority());
    }

    @Test
    void testEnumCount() {
        assertEquals(5, EventQueuePriority.values().length);
    }

}