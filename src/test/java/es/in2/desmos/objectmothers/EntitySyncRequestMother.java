package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.Id;

import java.util.stream.Stream;

public final class EntitySyncRequestMother {
    private EntitySyncRequestMother() {
    }

    public static Id[] createFromDataNegotiationResult(DataNegotiationResult dataNegotiationResult) {
        return Stream.concat(
                        dataNegotiationResult.newEntitiesToSync().stream().map(x -> new Id(x.id())),
                        dataNegotiationResult.existingEntitiesToSync().stream().map(x -> new Id(x.id())))
                .toArray(Id[]::new);
    }
}
