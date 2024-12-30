package es.in2.desmos.infrastructure.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

@ConfigurationProperties(prefix = "ngsi-subscription")
public record NgsiLdSubscriptionProperties(String notificationEndpoint, String subscriptionType, String idPrefix) {

    @ConstructorBinding
    public NgsiLdSubscriptionProperties(String notificationEndpoint, String subscriptionType, String idPrefix) {
        this.notificationEndpoint = Optional.ofNullable(notificationEndpoint).orElse("http://blockchain-connector-core:8080/notifications/broker");
        this.subscriptionType = Optional.ofNullable(subscriptionType).orElse("Subscription");
        this.idPrefix = Optional.ofNullable(idPrefix).orElse("urn:ngsi-ld:Subscription:");
    }

}
