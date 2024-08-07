package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public final class DiscoveryResponseMother {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DiscoveryResponseMother() {
    }

    public static String scorpioJson2List() throws JsonProcessingException, JSONException, NoSuchAlgorithmException {
        var mvEntity4DataNegotiations = List.of(MVEntity4DataNegotiationMother.sampleScorpio2());

        DiscoverySyncResponse discoverySyncResponse = new DiscoverySyncResponse("http://external-domain.org", mvEntity4DataNegotiations);

        return objectMapper.writeValueAsString(discoverySyncResponse);
    }

    public static String scorpioJson4List() throws JsonProcessingException, JSONException, NoSuchAlgorithmException {
        var mvEntity4DataNegotiations = List.of(MVEntity4DataNegotiationMother.sampleScorpio4());

        DiscoverySyncResponse discoverySyncResponse = new DiscoverySyncResponse("http://external-domain.org", mvEntity4DataNegotiations);

        return objectMapper.writeValueAsString(discoverySyncResponse);
    }
}
