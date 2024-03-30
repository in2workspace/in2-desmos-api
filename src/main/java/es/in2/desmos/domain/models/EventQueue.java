package es.in2.desmos.domain.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EventQueue implements Comparable<EventQueue> {

    private List<Object> event;
    private EventQueuePriority priority;

    @Override
    public int compareTo(EventQueue o) {
        return Integer.compare(this.priority.getPriority(), o.priority.getPriority());
    }

}
