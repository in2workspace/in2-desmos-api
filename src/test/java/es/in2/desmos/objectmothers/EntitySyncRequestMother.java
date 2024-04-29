package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.EntitySyncRequest;
import es.in2.desmos.domain.models.Id;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class EntitySyncRequestMother {
    private EntitySyncRequestMother() {
    }

    public static @NotNull EntitySyncRequest simple1and2() {
        List<Id> entities = new ArrayList<>();
        entities.add(new Id(MVEntity4DataNegotiationMother.sample1().id()));
        entities.add(new Id(MVEntity4DataNegotiationMother.sample2().id()));
        return new EntitySyncRequest(entities.toArray(Id[]::new));
    }

    public static @NotNull EntitySyncRequest simple3and4() {
        List<Id> entities = new ArrayList<>();
        entities.add(new Id(MVEntity4DataNegotiationMother.sample3().id()));
        entities.add(new Id(MVEntity4DataNegotiationMother.sample4().id()));
        return new EntitySyncRequest(entities.toArray(Id[]::new));
    }
}
