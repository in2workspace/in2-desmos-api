package es.in2.desmos.domain.models;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventQueue implements Comparable<EventQueue> {

    private @NotNull(message = "event must not be null") List<Object> event;
    private @NotNull(message = "priority must not be null") EventQueuePriority priority;

    @Override
    public int compareTo(EventQueue o) {
        return Integer.compare(this.priority.getPriority(), o.priority.getPriority());
    }

}