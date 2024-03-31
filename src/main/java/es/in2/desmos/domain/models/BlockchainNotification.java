package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record BlockchainNotification(
        @JsonProperty("id") @NotNull long id,
        @JsonProperty("publisherAddress") @NotNull String publisherAddress,
        @JsonProperty("eventType") @NotNull String eventType,
        @JsonProperty("timestamp") @NotNull long timestamp,
        @JsonProperty("dataLocation") @NotNull String dataLocation,
        @JsonProperty("relevantMetadata") @NotNull List<String> relevantMetadata,
        @JsonProperty("entityIDHash") @NotNull String entityId,
        @JsonProperty("previousEntityHash") @NotNull String previousEntityHash
) {
}
