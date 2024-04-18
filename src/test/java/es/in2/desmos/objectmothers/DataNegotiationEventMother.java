package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.DataNegotiationEvent;
import reactor.core.publisher.Mono;

public final class DataNegotiationEventMother {
    private DataNegotiationEventMother() {
    }

    public static DataNegotiationEvent empty() {
        return new DataNegotiationEvent(null, Mono.empty(), Mono.empty(), Mono.empty());
    }
}
