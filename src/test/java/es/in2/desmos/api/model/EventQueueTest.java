package es.in2.desmos.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventQueueTest {

    @Test
    void compareToShouldReturnNegativeWhenThisHasHigherPriority() {
        EventQueue highPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.SYNCHRONIZATION)
                .build();
        EventQueue lowPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION)
                .build();

        assertTrue(highPriorityEvent.compareTo(lowPriorityEvent) < 0, "High priority should be 'less' than low priority");
    }

    @Test
    void compareToShouldReturnPositiveWhenThisHasLowerPriority() {
        EventQueue lowPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION)
                .build();
        EventQueue highPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.SYNCHRONIZATION)
                .build();

        assertTrue(lowPriorityEvent.compareTo(highPriorityEvent) > 0, "Low priority should be 'greater' than high priority");
    }

    @Test
    void compareToShouldReturnZeroWhenPrioritiesAreEqual() {
        EventQueue firstMediumPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION)
                .build();
        EventQueue secondMediumPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION)
                .build();

        assertTrue(firstMediumPriorityEvent.compareTo(secondMediumPriorityEvent) == 0, "Equal priorities should result in compareTo returning zero");
    }
}

