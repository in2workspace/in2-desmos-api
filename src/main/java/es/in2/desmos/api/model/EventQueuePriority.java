package es.in2.desmos.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventQueuePriority {
    SYNCHRONIZATION(1),
    RECOVERPUBLISH(2),
    RECOVEREDIT(3),
    RECOVERDELETE(4),
    PUBLICATIONPUBLISH(5),
    PUBLICATIONEDIT(6),
    PUBLICATIONDELETE(7);

    private final int priority;
}
