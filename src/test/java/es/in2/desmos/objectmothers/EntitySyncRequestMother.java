package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.models.EntitySyncRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class EntitySyncRequestMother {
    private EntitySyncRequestMother() {
    }

    public static @NotNull EntitySyncRequest simpleEntitySyncRequest() {
        List<MVEntity4DataNegotiation> entities = new ArrayList<>();
        entities.add(MVEntity4DataNegotiationMother.sample1());
        entities.add(MVEntity4DataNegotiationMother.sample2());
        return new EntitySyncRequest(entities);
    }
}
