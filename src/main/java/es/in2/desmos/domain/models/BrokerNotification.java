package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record BrokerNotification(
        @JsonProperty("id") @NotBlank String id,
        @JsonProperty("type") @NotBlank String type,
        @JsonProperty("data") @NotNull List<@NotNull Map<@NotBlank String, Object>> data,
        @JsonProperty("subscriptionId") @NotBlank String subscriptionId,
        @JsonProperty("notifiedAt") @NotBlank String notifiedAt
) {
}