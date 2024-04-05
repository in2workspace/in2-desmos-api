package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncRequestMother {
    private DiscoverySyncRequestMother() {
    }

    public static @NotNull DiscoverySyncRequest simpleDiscoverySyncRequest() {
        String issuer = "https://my-domain.org";
        List<ProductOffering> productOfferingList = new ArrayList<>();
        productOfferingList.add(ProductOfferingMother.sample1());
        productOfferingList.add(ProductOfferingMother.sample2());
        return new DiscoverySyncRequest(issuer, productOfferingList);
    }
}
