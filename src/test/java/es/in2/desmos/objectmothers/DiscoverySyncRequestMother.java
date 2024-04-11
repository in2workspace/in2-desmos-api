package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.IdRecord;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncRequestMother {
    private DiscoverySyncRequestMother() {
    }

    public static @NotNull DiscoverySyncRequest list1And2() {
        String issuer = "https://my-domain.org";
        List<IdRecord> entityIds = new ArrayList<>();
        entityIds.add(new IdRecord(ProductOfferingMother.sample1().id()));
        entityIds.add(new IdRecord(ProductOfferingMother.sample2().id()));

        return new DiscoverySyncRequest(issuer, entityIds);
    }
}
