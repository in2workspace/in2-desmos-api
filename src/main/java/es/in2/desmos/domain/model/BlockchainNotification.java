package es.in2.desmos.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record BlockchainNotification(
        @JsonProperty("id") long id,
        @JsonProperty("publisherAddress") String publisherAddress,
        @JsonProperty("eventType") String eventType,
        @JsonProperty("timestamp") long timestamp,
        @JsonProperty("dataLocation") String dataLocation,
        @JsonProperty("relevantMetadata") List<String> relevantMetadata,
        @JsonProperty("entityIDHash") String entityId,
        @JsonProperty("previousEntityHash") String previousEntityHash
) {
}
