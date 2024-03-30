package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventQueueTest {

    @Test
    void testEquals() {
        EventQueue queue1 = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        EventQueue queue2 = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        assertEquals(queue1, queue2, "Both queues should be equal as they have the same state");
    }

    @Test
    void testHashCode() {
        EventQueue queue = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        assertEquals(queue.hashCode(), queue.hashCode(), "The hashCode should be consistent and equal for the same object");
    }

    @Test
    void testEqualsMethod() {
        // Arrange
        List<Object> eventList1 = List.of(new Object(), new Object());
        List<Object> eventList2 = List.of(new Object(), new Object());
        EventQueuePriority priority1 = EventQueuePriority.SYNCHRONIZATION;
        EventQueuePriority priority2 = EventQueuePriority.PUBLICATION_PUBLISH;
        // Act
        EventQueue eventQueue1 = EventQueue.builder().event(eventList1).priority(priority1).build();
        EventQueue eventQueue2 = EventQueue.builder().event(eventList1).priority(priority1).build();
        EventQueue eventQueue3 = EventQueue.builder().event(eventList2).priority(priority1).build();
        EventQueue eventQueue4 = EventQueue.builder().event(eventList1).priority(priority2).build();
        // Assert
        assertEquals(eventQueue1, eventQueue2, "Objects with same event list and priority should be equal");
        assertNotEquals(eventQueue1, eventQueue3, "Objects with different event lists should not be equal");
        assertNotEquals(eventQueue1, eventQueue4, "Objects with different priorities should not be equal");
        // Consistency check
        assertEquals(eventQueue1, eventQueue1, "Objects should be equal to themselves");
        assertNotEquals(eventQueue1, new Object(), "Objects should not be equal to an object of a different type");
    }

    @Test
    void testToString() {
        EventQueue queue = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        assertNotNull(queue.toString(), "The toString method should return a non-null string representation");
    }

    @Test
    void testSetters() {
        List<Object> eventList = List.of();
        EventQueue queue = EventQueue.builder().build();
        queue.setEvent(eventList);
        queue.setPriority(EventQueuePriority.PUBLICATION_PUBLISH);
        assertEquals(eventList, queue.getEvent(), "The setEvent method should set the event list");
        assertEquals(EventQueuePriority.PUBLICATION_PUBLISH, queue.getPriority(), "The setPriority method should set the priority");
    }

    @Test
    void testGetters() {
        List<Object> eventList = List.of();
        EventQueue queue = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .event(eventList)
                .build();
        assertEquals(eventList, queue.getEvent(), "The getEvent method should return the correct event list");
        assertEquals(EventQueuePriority.PUBLICATION_PUBLISH, queue.getPriority(), "The getPriority method should return the correct priority");
    }

    @Test
    void testCanEqual() {
        EventQueue queue = EventQueue.builder().build();
        assertTrue(queue.canEqual(EventQueue.builder().build()), "The canEqual method should verify that the other object is an instance of the class EventQueue");
    }

    @Test
    void compareToShouldReturnNegativeWhenPriorityIsHigher() {
        EventQueue highPriorityQueue = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.SYNCHRONIZATION)
                .build();
        EventQueue lowPriorityQueue = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        assertTrue(highPriorityQueue.compareTo(lowPriorityQueue) < 0,
                "The compareTo method should return a negative value when the priority is higher");
    }

    @Test
    void compareToShouldReturnPositiveWhenPriorityIsLower() {
        EventQueue highPriorityQueue = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.SYNCHRONIZATION)
                .build();
        EventQueue lowPriorityQueue = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        assertTrue(lowPriorityQueue.compareTo(highPriorityQueue) > 0,
                "The compareTo method should return a positive value when the priority is lower");
    }

    @Test
    void compareToShouldReturnZeroWhenPrioritiesAreEqual() {
        EventQueue firstQueue = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        EventQueue secondQueue = EventQueue.builder()
                .event(List.of())
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        assertEquals(0, firstQueue.compareTo(secondQueue),
                "The compareTo method should return 0 when the priorities are equal");
    }

    @Test
    void EventQueueEventQueueBuilderToString() {
        // Arrange
        String expectedToString = "EventQueue.EventQueueBuilder(event=null, priority=null)";
        // Act
        EventQueue.EventQueueBuilder eventQueueBuilder = EventQueue.builder();
        // Assert
        assertNotNull(eventQueueBuilder.toString(), "The toString method should return a non-null string representation");
        assertEquals(expectedToString, eventQueueBuilder.toString());
    }

}
