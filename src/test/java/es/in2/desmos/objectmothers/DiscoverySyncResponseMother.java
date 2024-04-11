package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncResponseMother {
    private DiscoverySyncResponseMother() {
    }

    public static @NotNull DiscoverySyncResponse list3And4(String contextBrokerExternalDomain) {
        List<ProductOffering> productOfferingList = new ArrayList<>();
        productOfferingList.add(ProductOfferingMother.sample3());
        productOfferingList.add(ProductOfferingMother.sample4());
        return new DiscoverySyncResponse(contextBrokerExternalDomain, productOfferingList);
    }

    public static @NotNull DiscoverySyncResponse fromList(String contextBrokerExternalDomain, List<ProductOffering> productOfferings) {
        return new DiscoverySyncResponse(contextBrokerExternalDomain, productOfferings);
    }
}
