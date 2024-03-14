package es.in2.desmos.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventQueuePriority {
    SYNCHRONIZATION(1),
    RECOVER_PUBLISH(2),
    RECOVER_EDIT(3),
    RECOVER_DELETE(4),
    PUBLICATION_PUBLISH(5),
    PUBLICATION_EDIT(6),
    PUBLICATION_DELETE(7);

    private final int priority;

}
