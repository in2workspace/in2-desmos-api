package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.models.EntitySyncRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class EntitySyncRequestMother {
    private EntitySyncRequestMother() {
    }

    public static @NotNull EntitySyncRequest simpleEntitySyncRequest() {
        List<Entity> entities = new ArrayList<>();
        entities.add(EntityMother.sample1());
        entities.add(EntityMother.sample2());
        return new EntitySyncRequest(entities);
    }
}
