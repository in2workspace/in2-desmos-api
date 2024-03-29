package es.in2.desmos.infrastructure.broker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrokerSubscription(
        @JsonProperty("id") String id,
        @JsonProperty("type") String type,
        @JsonProperty("entities") List<Entity> entities,
        @JsonProperty("notification") SubscriptionNotification notification
) {

    @Builder
    public record Entity(String type) {
    }

    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SubscriptionNotification(@JsonProperty("endpoint") SubscriptionEndpoint subscriptionEndpoint) {

        @Builder
        public record SubscriptionEndpoint(
                @JsonProperty("uri") String uri,
                @JsonProperty("accept") String accept,
                @JsonProperty("receiverInfo") List<RetrievalInfoContentType> receiverInfo) {

                    @Builder
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    public record RetrievalInfoContentType(@JsonProperty("Content-Type") String contentType) {
                    }

        }

    }


}
