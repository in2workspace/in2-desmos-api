package es.in2.desmos.infrastructure.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Event Subscription Properties
 *
 * @param notificationEndpoint - endpoint to notify on events
 */
@ConfigurationProperties(prefix = "tx-subscription")
public record TxSubscriptionProperties(String notificationEndpoint) {
}
