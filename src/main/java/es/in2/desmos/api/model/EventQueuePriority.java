package es.in2.desmos.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventQueuePriority {
    SYNCHRONIZATION(1),
    PUBLICATION(2);

    private final int priority;
}
