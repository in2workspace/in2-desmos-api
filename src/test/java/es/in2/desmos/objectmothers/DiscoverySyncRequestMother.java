package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncRequestMother {
    private DiscoverySyncRequestMother() {
    }

    public static @NotNull DiscoverySyncRequest list1And2() {
        String issuer = "https://my-domain.org";
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationIds = new ArrayList<>();
        MVEntity4DataNegotiationIds.add(MVEntity4DataNegotiationMother.sample1());
        MVEntity4DataNegotiationIds.add(MVEntity4DataNegotiationMother.sample2());

        return new DiscoverySyncRequest(issuer, MVEntity4DataNegotiationIds);
    }

    public static @NotNull DiscoverySyncRequest fullList(String issuer) {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationIds =
                new ArrayList<>(MVEntity4DataNegotiationMother.fullList());

        return new DiscoverySyncRequest(issuer, MVEntity4DataNegotiationIds);
    }
}
