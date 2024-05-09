package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.DiscoverySyncResponse;

import java.util.List;

public final class DiscoveryResponseMother {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DiscoveryResponseMother() {
    }

    public static String json2List() throws JsonProcessingException {
        var mvEntity4DataNegotiations = List.of(MVEntity4DataNegotiationMother.sample2());

        DiscoverySyncResponse discoverySyncResponse = new DiscoverySyncResponse("http://external-domain.org", mvEntity4DataNegotiations);

        return objectMapper.writeValueAsString(discoverySyncResponse);
    }

    public static String json4List() throws JsonProcessingException {
        var mvEntity4DataNegotiations = List.of(MVEntity4DataNegotiationMother.sample4());

        DiscoverySyncResponse discoverySyncResponse = new DiscoverySyncResponse("http://external-domain.org", mvEntity4DataNegotiations);

        return objectMapper.writeValueAsString(discoverySyncResponse);
    }
}
