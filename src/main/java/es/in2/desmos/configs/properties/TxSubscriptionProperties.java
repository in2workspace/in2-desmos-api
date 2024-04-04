package es.in2.desmos.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Event Subscription Properties
 *
 * @param notificationEndpoint - endpoint to notify on events
 * @param metadataEVM          - metadata to include in events
 * @param entityTypes          - type of events to subscribe to
 */
@ConfigurationProperties(prefix = "tx-subscription")
public record TxSubscriptionProperties(String notificationEndpoint, String metadataEVM, List<String> entityTypes) {
}