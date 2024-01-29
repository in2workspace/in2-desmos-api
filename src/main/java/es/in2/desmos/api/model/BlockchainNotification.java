package es.in2.desmos.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record BlockchainNotification(
        @JsonProperty("id") Id id,
        @JsonProperty("publisherAddress") String publisherAddress,
        @JsonProperty("eventType") String eventType,
        @JsonProperty("timestamp") Timestamp timestamp,
        @JsonProperty("dataLocation") String dataLocation,
        @JsonProperty("relevantMetadata") List<String> relevantMetadata
) {

    @Builder
    public record Id(String type, String hex) {}

    @Builder
    public record Timestamp(String type, String hex) {}

}
