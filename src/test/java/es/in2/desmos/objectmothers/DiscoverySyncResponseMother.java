package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncResponseMother {
    private DiscoverySyncResponseMother() {
    }

    public static @NotNull DiscoverySyncResponse list3And4(String contextBrokerExternalDomain) {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(EntityMother.sample3());
        entityList.add(EntityMother.sample4());
        return new DiscoverySyncResponse(contextBrokerExternalDomain, entityList);
    }

    public static @NotNull DiscoverySyncResponse fromList(String contextBrokerExternalDomain, List<Entity> entities) {
        return new DiscoverySyncResponse(contextBrokerExternalDomain, entities);
    }
}
