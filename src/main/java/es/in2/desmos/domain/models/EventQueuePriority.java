package es.in2.desmos.domain.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventQueuePriority {

    CRITICAL(1),
    HIGH(2),
    MEDIUM(3),
    LOW(4),
    MINOR(5);

    private final int priority;

}
