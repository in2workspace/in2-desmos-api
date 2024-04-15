package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record BlockchainSubscription(
        @JsonProperty("eventTypes")
        @NotNull(message = "eventTypes must not be null")
        List<@NotBlank(message = "eventTypes must not be blank") String> eventTypes,

        @JsonProperty("notificationEndpoint")
        @NotBlank(message = "notificationEndpoint must not be blank")
        String notificationEndpoint
) {
}