package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.DiscoverySyncRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncRequestMother {
    private DiscoverySyncRequestMother() {
    }

    public static @NotNull DiscoverySyncRequest simpleDiscoverySyncRequest() {
        String issuer = "https://my-domain.org";
        List<String> entityIds = new ArrayList<>();
        entityIds.add(ProductOfferingMother.sample1().id());
        entityIds.add(ProductOfferingMother.sample2().id());
        var externalEntityIds = DiscoverySyncRequest.createExternalEntityIdsListFromString(entityIds);
        return new DiscoverySyncRequest(issuer, externalEntityIds);
    }
}
