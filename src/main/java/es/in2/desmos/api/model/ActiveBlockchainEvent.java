package es.in2.desmos.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActiveBlockchainEvent(
    @JsonProperty("id") long id,
    @JsonProperty("timestamp") long timestamp,
    @JsonProperty("eventType") String eventType,
    @JsonProperty("dataLocation") String dataLocation,
    @JsonProperty("relevantMetadata") List<String> relevantMetadata,
    @JsonProperty("entityId") String entityId,
    @JsonProperty("previousEntityHash") String previousEntityHash
) {}