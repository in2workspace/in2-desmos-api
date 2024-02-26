package es.in2.desmos.blockchain.config;

import es.in2.desmos.api.exception.RequestErrorException;
import es.in2.desmos.blockchain.config.properties.EventSubscriptionProperties;
import es.in2.desmos.blockchain.model.DLTAdapterSubscription;
import es.in2.desmos.blockchain.service.DLTAdapterSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableRetry
public class DLTAdapterSubscriptionInitializer {

    private final EventSubscriptionProperties eventSubscriptionProperties;
    private final DLTAdapterSubscriptionService dltAdapterSubscriptionService;

    @EventListener(ApplicationReadyEvent.class)
    @Retryable(retryFor = RequestErrorException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000)
    )
    public Mono<Void> initializeSubscriptions() {
        String processId = UUID.randomUUID().toString();
        return setBlockchainEventSubscription(processId);
    }

    private Mono<Void> setBlockchainEventSubscription(String processId) {
        log.info("Setting Blockchain Event Subscription...");
        // Create the EVM Subscription object
        DLTAdapterSubscription dltAdapterSubscription = DLTAdapterSubscription.builder()
                .eventTypes(eventSubscriptionProperties.eventTypes())
                .notificationEndpoint(eventSubscriptionProperties.notificationEndpoint())
                .build();
        // Create the subscription
        return dltAdapterSubscriptionService.createSubscription(processId, dltAdapterSubscription)
                .doOnSuccess(response -> log.info("Blockchain Event Subscription created successfully"))
                .doOnError(e -> log.error("Error creating Blockchain Event Subscription", e));
    }

    @Recover
    public void recover(RequestErrorException e) {
        log.error("After retries, subscription failed", e);
    }

}
