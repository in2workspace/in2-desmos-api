package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.EventQueue;
import es.in2.desmos.domain.models.EventQueuePriority;

import java.util.List;

public final class EventQueueMother {
    private EventQueueMother() {
    }

    public static EventQueue basicEventQueue(String name){
        return EventQueue.builder()
                .event(List.of(name))
                .priority(EventQueuePriority.MEDIUM)
                .build();
    }
}
