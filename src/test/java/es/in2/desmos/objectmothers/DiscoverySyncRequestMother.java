package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncRequestMother {
    private DiscoverySyncRequestMother() {
    }

    public static @NotNull DiscoverySyncRequest list1And2() {
        String issuer = "https://my-domain.org";
        List<Entity> entityIds = new ArrayList<>();
        entityIds.add(EntityMother.sample1());
        entityIds.add(EntityMother.sample2());

        return new DiscoverySyncRequest(issuer, entityIds);
    }
}
