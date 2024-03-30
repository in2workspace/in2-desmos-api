package es.in2.desmos.runners;

import es.in2.desmos.configs.BlockchainConfig;
import es.in2.desmos.configs.BrokerConfig;
import es.in2.desmos.services.blockchain.BlockchainListenerService;
import es.in2.desmos.services.broker.BrokerListenerService;
import es.in2.desmos.domain.exceptions.RequestErrorException;
import es.in2.desmos.domain.models.BrokerSubscription;
import es.in2.desmos.domain.models.BlockchainSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitialSubscriptionsSetup {

    private final BrokerConfig brokerConfig;
    private final BlockchainConfig blockchainConfig;
    private final BrokerListenerService brokerListenerService;
    private final BlockchainListenerService blockchainListenerService;

    private static final String SUBSCRIPTION_ID_PREFIX = "urn:ngsi-ld:Subscription:";
    private static final String SUBSCRIPTION_TYPE = "Subscription";

    @EventListener(ApplicationReadyEvent.class)
    @Retryable(retryFor = RequestErrorException.class, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    public Mono<Void> setBrokerSubscription() {
        // Set the processId to a random UUID
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Setting Broker Entities Subscription...", processId);
        // Build Entity Type List to subscribe to
        List<BrokerSubscription.Entity> entities = new ArrayList<>();
        brokerConfig.getEntityTypes().forEach(entityType -> entities.add(BrokerSubscription.Entity.builder().type(entityType).build()));
        // Create the Broker Subscription object
        BrokerSubscription brokerSubscription = BrokerSubscription.builder()
                .id(SUBSCRIPTION_ID_PREFIX + UUID.randomUUID())
                .type(SUBSCRIPTION_TYPE)
                .entities(entities)
                .notification(BrokerSubscription.SubscriptionNotification.builder()
                        .subscriptionEndpoint(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.builder()
                                .uri(brokerConfig.getNotificationEndpoint())
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .receiverInfo(List.of(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .build()))
                                .build())
                        .build())
                .build();
        // Create the subscription and log the result
        log.debug("ProcessID: {} - Broker Subscription: {}", processId, brokerSubscription);
        return brokerListenerService.createSubscription(processId, brokerSubscription)
                .doOnSuccess(response -> log.info("ProcessID: {} - Broker Entities Subscription created successfully", processId))
                .doOnError(e -> log.error("ProcessID: {} - Error creating Broker Entities Subscription", processId, e));
    }

    @EventListener(ApplicationReadyEvent.class)
    @Retryable(retryFor = RequestErrorException.class, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    public Mono<Void> setBlockchainSubscription() {
        log.info("Setting Blockchain Event Subscription...");
        String processId = UUID.randomUUID().toString();
        // Create the EVM Subscription object
        BlockchainSubscription blockchainSubscription = BlockchainSubscription.builder()
                .eventTypes(blockchainConfig.getEntityTypes())
                .notificationEndpoint(blockchainConfig.getNotificationEndpoint())
                .build();
        // Create the subscription
        return blockchainListenerService.createSubscription(processId, blockchainSubscription)
                .doOnSuccess(response -> log.info("Blockchain Event Subscription created successfully"))
                .doOnError(e -> log.error("Error creating Blockchain Event Subscription", e));
    }

    // TODO: Implement the recover method
    @Recover
    public void recover(RequestErrorException e) {
        log.error("After retries, subscription failed", e);
    }

}
