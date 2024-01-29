package es.in2.desmos.broker.config;


import es.in2.desmos.broker.config.properties.NgsiLdSubscriptionProperties;
import es.in2.desmos.broker.model.BrokerSubscription;
import es.in2.desmos.broker.service.BrokerSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BrokerSubscriptionInitializer {

    private final NgsiLdSubscriptionProperties subscriptionConfiguration;
    private final BrokerSubscriptionService brokerSubscriptionService;

    private static final String SUBSCRIPTION_ID_PREFIX = "urn:ngsi-ld:Subscription:";
    private static final String SUBSCRIPTION_TYPE = "Subscription";

    @EventListener(ApplicationReadyEvent.class)
    public Mono<Void> setBrokerEntitySubscription() {
        // Set the processId to a random UUID
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Setting Broker Entities Subscription...", processId);

        // Build Entity Type List to subscribe to
        List<BrokerSubscription.Entity> entities = new ArrayList<>();
        subscriptionConfiguration.entityTypes().forEach(entityType -> entities.add(BrokerSubscription.Entity.builder().type(entityType).build()));

        // Create the Broker Subscription object
        BrokerSubscription brokerSubscription = BrokerSubscription.builder()
                .id(SUBSCRIPTION_ID_PREFIX + UUID.randomUUID())
                .type(SUBSCRIPTION_TYPE)
                .entities(entities)
                .notification(BrokerSubscription.SubscriptionNotification.builder()
                        .subscriptionEndpoint(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.builder()
                                .uri(subscriptionConfiguration.notificationEndpoint())
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .receiverInfo(List.of(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .build()))
                                .build())
                        .build())
                .build();

        // Create the subscription and log the result
        log.debug("ProcessID: {} - Broker Subscription: {}", processId, brokerSubscription);
        return brokerSubscriptionService.createSubscription(processId, brokerSubscription)
                .doOnSuccess(response -> log.info("ProcessID: {} - Broker Entities Subscription created successfully", processId))
                .doOnError(e -> log.error("ProcessID: {} - Error creating Broker Entities Subscription", processId, e));
    }

}
