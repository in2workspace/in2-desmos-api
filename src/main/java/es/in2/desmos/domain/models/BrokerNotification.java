package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record BrokerNotification(
        @JsonProperty("id") @NotNull String id,
        @JsonProperty("type") @NotNull String type,
        @JsonProperty("data") @NotNull List<Map<String, Object>> data,
        @JsonProperty("subscriptionId") @NotNull String subscriptionId,
        @JsonProperty("notifiedAt") @NotNull String notifiedAt
) {
}
