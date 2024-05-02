package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record BlockchainSubscription(
        @JsonProperty("eventTypes") List<String> eventTypes,
        @JsonProperty("notificationEndpoint") String notificationEndpoint
) {
}