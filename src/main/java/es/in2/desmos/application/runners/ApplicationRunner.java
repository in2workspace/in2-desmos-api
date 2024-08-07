package es.in2.desmos.application.runners;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import es.in2.desmos.domain.exceptions.RequestErrorException;
import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.models.BrokerSubscription;
import es.in2.desmos.domain.services.api.SubscriptionManagerService;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.BlockchainConfig;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static es.in2.desmos.domain.utils.ApplicationConstants.SUBSCRIPTION_ID_PREFIX;
import static es.in2.desmos.domain.utils.ApplicationConstants.SUBSCRIPTION_TYPE;
import static es.in2.desmos.domain.utils.ApplicationUtils.getEnvironmentMetadata;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationRunner {

    private final ApiConfig apiConfig;
    private final BrokerConfig brokerConfig;
    private final BlockchainConfig blockchainConfig;
    private final BrokerListenerService brokerListenerService;
    private final BlockchainListenerService blockchainListenerService;
    private final DataSyncWorkflow dataSyncWorkflow;
    private final SubscriptionManagerService subscriptionManagerService;
    private final AtomicBoolean isQueueAuthorizedForEmit = new AtomicBoolean(false);

    @EventListener(ApplicationReadyEvent.class)
    public Mono<Void> onApplicationReady() {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Setting initial configurations...", processId);
        return setBrokerSubscription(processId)
                .then(setBlockchainSubscription(processId))
                .thenMany(initializeDataSync(processId))
                .then();
    }

    @Retryable(retryFor = RequestErrorException.class, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    private Mono<Void> setBrokerSubscription(String processId) {
        log.info("ProcessID: {} - Setting Broker Subscription...", processId);
        // Build Entity Type List to subscribe to
        List<BrokerSubscription.Entity> entities = new ArrayList<>();
        brokerConfig.getEntityTypes().forEach(entityType -> entities.add(BrokerSubscription.Entity.builder()
                .type(entityType).build()));
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
                .doOnSuccess(response -> log.info("ProcessID: {} - Broker Subscription created successfully.", processId))
                .doOnError(e -> log.error("ProcessID: {} - Error creating Broker Subscription", processId, e));
    }

    @Retryable(retryFor = RequestErrorException.class, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    private Mono<Void> setBlockchainSubscription(String processId) {
        log.info("ProcessID: {} - Setting Blockchain Subscription...", processId);
        // Create the Blockchain Subscription object
        BlockchainSubscription blockchainSubscription = BlockchainSubscription.builder()
                .eventTypes(blockchainConfig.getEntityTypes())
                .metadata(List.of(getEnvironmentMetadata(apiConfig.getCurrentEnvironment())))
                .notificationEndpoint(blockchainConfig.getNotificationEndpoint())
                .build();
        // Create the subscription
        return blockchainListenerService.createSubscription(processId, blockchainSubscription)
                .doOnSuccess(response -> log.info("ProcessID: {} - Blockchain Subscription created successfully.", processId))
                .doOnError(e -> log.error("ProcessID: {} - Error creating Blockchain Subscription", processId, e));
    }

    private Flux<Void> initializeDataSync(String processId) {
        log.info("ProcessID: {} - Initializing Data Synchronization Workflow...", processId);
        // Start data synchronization process
        return dataSyncWorkflow.startDataSyncWorkflow(processId)
                .doOnComplete(() -> {
                    log.info("ProcessID: {} - Data Synchronization Workflow has finished.", processId);
                    log.info("ProcessID: {} - Authorizing queues for Pub-Sub Workflows...", processId);
                    isQueueAuthorizedForEmit.set(true);
                })
                .doOnTerminate(() -> {
                    initializeQueueProcessing(processId);
                    log.info("ProcessID: {} - Queues have been authorized and enabled.", processId);
                });
    }

    private void initializeQueueProcessing(String processId) {
        if (!isQueueProcessingAuthorized()) {
            log.debug("ProcessID: {} - Queue processing is currently paused.", processId);
            return;
        }
        log.debug("ProcessID: {} - Starting queue processing...", processId);
        restartQueueProcessing(processId);
    }

    private boolean isQueueProcessingAuthorized() {
        return isQueueAuthorizedForEmit.get();
    }

    private void restartQueueProcessing(String processId) {
        log.debug("ProcessID: {} - Restarting queue processing...", processId);
        subscriptionManagerService.restartPublishSubscription(processId);
        subscriptionManagerService.restartSubscribeSubscription(processId);
    }
}
