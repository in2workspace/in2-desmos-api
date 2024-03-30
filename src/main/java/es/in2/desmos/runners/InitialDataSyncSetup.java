package es.in2.desmos.runners;

import es.in2.desmos.services.blockchain.BlockchainPublisherService;
import es.in2.desmos.services.broker.BrokerPublisherService;
import es.in2.desmos.services.sync.DataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitialDataSyncSetup {

    private final DataSyncService dataSyncService;
    private final BlockchainPublisherService blockchainPublisherService;
    private final BrokerPublisherService brokerPublisherService;

    private Disposable blockchainPublisherSubscriptionQueue;
    private Disposable brokerPublisherSubscriptionQueue;

    /**
     * Flag to control if the queues can emit messages.
     * This is used to avoid emitting messages before the initial synchronization is finished.
     **/
    private final AtomicBoolean isQueueAuthorizedForEmit = new AtomicBoolean(false);

    @EventListener(ApplicationReadyEvent.class)
    public Mono<Void> initializeDataSync() {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Initializing Data Synchronization...", processId);
        // Start data synchronization process
        return dataSyncService.synchronizeData(processId)
                // When the synchronization is finished, enable queue to process the
                // data synchronization using pub-sub.
                .doOnTerminate(() -> {
                    log.info("Data Synchronization finished. Authorizing queue to emit data...");
                    isQueueAuthorizedForEmit.set(true);
                    initializeQueueProcessing();
                    log.info("Queue has been enabled.");
                });
    }

    private void initializeQueueProcessing() {
        if (!isQueueProcessingAuthorized()) {
            log.debug("Queue processing is currently paused.");
            return;
        }
        log.debug("Starting queue processing...");
        restartQueueProcessing();
    }

    private boolean isQueueProcessingAuthorized() {
        return isQueueAuthorizedForEmit.get();
    }

    private void restartQueueProcessing() {
        resetActiveSubscriptions();
        startBlockchainEventProcessing();
        startBrokerEventProcessing();
    }

    private void resetActiveSubscriptions() {
        disposeIfActive(blockchainPublisherSubscriptionQueue);
        disposeIfActive(brokerPublisherSubscriptionQueue);
    }

    private void startBlockchainEventProcessing() {
        blockchainPublisherSubscriptionQueue = blockchainPublisherService.publishDataToBlockchain()
                .subscribe(
                        null,
                        error -> log.error("Error occurred during blockchain event processing"),
                        () -> log.info("Blockchain event processing completed")
                );
    }

    private void startBrokerEventProcessing() {
        brokerPublisherSubscriptionQueue = brokerPublisherService.publishDataToBroker()
                .subscribe(
                        null,
                        error -> log.error("Error occurred during broker entity event processing"),
                        () -> log.info("Broker entity event processing completed")
                );
    }

    private void disposeIfActive(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

}
