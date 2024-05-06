package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;

public final class DiscoveryResponseMother {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DiscoveryResponseMother() {
    }

    public static String json2List() throws JsonProcessingException {
        var mvEntity4DataNegotiations = new MVEntity4DataNegotiation[]{
                MVEntity4DataNegotiationMother.sample2(),
        };

        return objectMapper.writeValueAsString(mvEntity4DataNegotiations);
    }

    public static String json4List() throws JsonProcessingException {
        var mvEntity4DataNegotiations = new MVEntity4DataNegotiation[]{
                MVEntity4DataNegotiationMother.sample4(),
        };

        return objectMapper.writeValueAsString(mvEntity4DataNegotiations);
    }
}
