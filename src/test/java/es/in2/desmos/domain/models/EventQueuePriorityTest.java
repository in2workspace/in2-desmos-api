package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventQueuePriorityTest {

    @Test
    void testEnumValues() {
        assertEquals(1, EventQueuePriority.SYNCHRONIZATION.getPriority());
        assertEquals(2, EventQueuePriority.RECOVER_PUBLISH.getPriority());
        assertEquals(3, EventQueuePriority.RECOVER_EDIT.getPriority());
        assertEquals(4, EventQueuePriority.RECOVER_DELETE.getPriority());
        assertEquals(5, EventQueuePriority.PUBLICATION_PUBLISH.getPriority());
        assertEquals(6, EventQueuePriority.PUBLICATION_EDIT.getPriority());
        assertEquals(7, EventQueuePriority.PUBLICATION_DELETE.getPriority());
    }

    @Test
    void testEnumCount() {
        assertEquals(7, EventQueuePriority.values().length);
    }


}
