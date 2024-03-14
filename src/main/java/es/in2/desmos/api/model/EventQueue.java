package es.in2.desmos.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventQueue implements Comparable<EventQueue> {

    private List<Object> event;
    private EventQueuePriority priority;

    @Override
    public int compareTo(EventQueue o) {
        return Integer.compare(this.priority.getPriority(), o.priority.getPriority());
    }

}
