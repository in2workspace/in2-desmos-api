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
        @JsonProperty("id")
        @NotBlank(message = "id must not be blank")
        String id,

        @JsonProperty("type")
        @NotBlank(message = "type must not be blank")
        String type,

        @JsonProperty("entities")
        @NotNull(message = "entities must not be null")
        List<Entity> entities,

        @JsonProperty("notification")
        @NotNull(message = "notification must not be null")
        SubscriptionNotification notification
) {

    @Builder
    public record Entity(
            @NotBlank(message = "type must not be blank")
            String type
    ) {
    }

    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SubscriptionNotification(
            @JsonProperty("endpoint")
            @NotNull(message = "endpoint must not be null")
            SubscriptionEndpoint subscriptionEndpoint) {

        @Builder
        public record SubscriptionEndpoint(
                @JsonProperty("uri")
                @NotBlank(message = "uri must not be blank")
                String uri,

                @JsonProperty("accept")
                @NotBlank(message = "accept must not be blank")
                String accept,

                @JsonProperty("receiverInfo")
                @NotNull(message = "receiverInfo must not be null")
                List<@NotNull RetrievalInfoContentType> receiverInfo) {

            @Builder
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record RetrievalInfoContentType(
                    @JsonProperty("Content-Type")
                    @NotBlank(message = "Content-Type must not be blank")
                    String contentType) {
            }

        }

    }


}