package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.EventQueue;
import es.in2.desmos.domain.models.EventQueuePriority;

import java.util.List;

public final class EnqueueEventMother {
    private EnqueueEventMother() {
    }

    public static EventQueue sample(List<Object> events) {
        return EventQueue.builder()
                .event(events)
                .priority(EventQueuePriority.MEDIUM)
                .build();
    }
}
