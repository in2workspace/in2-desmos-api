package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.models.EntitySyncRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class EntitySyncRequestMother {
    private EntitySyncRequestMother() {
    }

    public static @NotNull EntitySyncRequest simple1and2() {
        List<MVEntity4DataNegotiation> entities = new ArrayList<>();
        entities.add(MVEntity4DataNegotiationMother.sample1());
        entities.add(MVEntity4DataNegotiationMother.sample2());
        return new EntitySyncRequest(entities.toArray(MVEntity4DataNegotiation[]::new));
    }

    public static @NotNull EntitySyncRequest simple3and4() {
        List<MVEntity4DataNegotiation> entities = new ArrayList<>();
        entities.add(MVEntity4DataNegotiationMother.sample3());
        entities.add(MVEntity4DataNegotiationMother.sample4());
        return new EntitySyncRequest(entities.toArray(MVEntity4DataNegotiation[]::new));
    }
}
