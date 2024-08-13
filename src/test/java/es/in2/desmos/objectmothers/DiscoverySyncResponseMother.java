package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncResponseMother {
    private DiscoverySyncResponseMother() {
    }

    public static @NotNull DiscoverySyncResponse list3And4(String contextBrokerExternalDomain) throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
        MVEntity4DataNegotiationList.add(MVEntity4DataNegotiationMother.sample3());
        MVEntity4DataNegotiationList.add(MVEntity4DataNegotiationMother.sample4());
        return new DiscoverySyncResponse(contextBrokerExternalDomain, MVEntity4DataNegotiationList);
    }

    public static @NotNull DiscoverySyncResponse fromList(String contextBrokerExternalDomain, List<MVEntity4DataNegotiation> entities) {
        return new DiscoverySyncResponse(contextBrokerExternalDomain, entities);
    }
}
