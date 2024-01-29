package es.in2.desmos.blockchain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record BlockchainAdapterSubscription(
        @JsonProperty("eventTypes") List<String> eventTypes,
        @JsonProperty("notificationEndpoint") String notificationEndpoint
) {
}
