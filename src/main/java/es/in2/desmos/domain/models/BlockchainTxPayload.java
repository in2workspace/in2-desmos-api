package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record BlockchainTxPayload(
        @JsonProperty("eventType") String eventType,
        @JsonProperty("iss") String organizationIdentifier,
        @JsonProperty("entityId") String entityId,
        @JsonProperty("previousEntityHash") String previousEntityHashLink,
        @JsonProperty("dataLocation") String dataLocation,
        @JsonProperty("relevantMetadata") List<String> metadata
) {
}