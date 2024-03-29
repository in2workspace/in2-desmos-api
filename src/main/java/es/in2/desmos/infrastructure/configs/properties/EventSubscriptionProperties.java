package es.in2.desmos.infrastructure.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Event Subscription Properties
 *
 * @param notificationEndpoint - endpoint to notify on events
 * @param eventTypes           - type of events to subscribe to
 */
@ConfigurationProperties(prefix = "event-subscription")
public record EventSubscriptionProperties(String notificationEndpoint, List<String> eventTypes) {
}
