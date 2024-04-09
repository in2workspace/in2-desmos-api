package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrokerSubscription(
        @JsonProperty("id") @NotBlank String id,
        @JsonProperty("type") @NotBlank String type,
        @JsonProperty("entities") @NotNull List<Entity> entities,
        @JsonProperty("notification") @NotNull SubscriptionNotification notification
) {

    @Builder
    public record Entity(@NotBlank String type) {
    }

    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SubscriptionNotification(
            @JsonProperty("endpoint") @NotNull SubscriptionEndpoint subscriptionEndpoint) {

        @Builder
        public record SubscriptionEndpoint(
                @JsonProperty("uri") @NotBlank String uri,
                @JsonProperty("accept") @NotBlank String accept,
                @JsonProperty("receiverInfo") @NotNull List<@NotNull RetrievalInfoContentType> receiverInfo) {

            @Builder
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record RetrievalInfoContentType(@JsonProperty("Content-Type") @NotBlank String contentType) {
            }

        }

    }


}