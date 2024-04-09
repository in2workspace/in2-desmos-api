package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record BlockchainSubscription(
        @JsonProperty("eventTypes") @NotNull List<@NotBlank String> eventTypes,
        @JsonProperty("notificationEndpoint") @NotBlank String notificationEndpoint
) {
}