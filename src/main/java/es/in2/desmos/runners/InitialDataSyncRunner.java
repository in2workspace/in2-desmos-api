package es.in2.desmos.runners;

import es.in2.desmos.workflows.DataSyncWorkflow;
import es.in2.desmos.workflows.PublishWorkflow;
import es.in2.desmos.workflows.SubscribeWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitialDataSyncRunner {

    private final DataSyncWorkflow dataSyncWorkflow;
    private final PublishWorkflow publishWorkflow;
    private final SubscribeWorkflow subscribeWorkflow;

    private Disposable publishQueueDisposable;
    private Disposable subscribeQueueDisposable;
    private final AtomicBoolean isQueueAuthorizedForEmit = new AtomicBoolean(false);

    @EventListener(ApplicationReadyEvent.class)
    public Flux<Void> initializeDataSync() {
        // Set up the initial data synchronization process
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Initializing Data Synchronization Workflow...", processId);
        // Start data synchronization process
        return dataSyncWorkflow.startDataSyncWorkflow(processId)
                // When the synchronization is finished, enable queue to process the
                // data synchronization using pub-sub.
                .doOnTerminate(() -> {
                    log.info("Data Synchronization Workflow has finished. " +
                            "Authorizing queues for Pub-Sub Workflows...");
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
        disposeIfActive(publishQueueDisposable);
        disposeIfActive(subscribeQueueDisposable);
    }

    private void disposeIfActive(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    private void startBlockchainEventProcessing() {
        publishQueueDisposable = publishWorkflow.startPublishWorkflow()
                .subscribe(
                        null,
                        error -> log.error("Error occurred during Publish Workflow"),
                        () -> log.info("Publish Workflow completed")
                );
    }

    private void startBrokerEventProcessing() {
        subscribeQueueDisposable = subscribeWorkflow.startSubscribeWorkflow()
                .subscribe(
                        null,
                        error -> log.error("Error occurred during Subscribe Workflow"),
                        () -> log.info("Subscribe Workflow completed")
                );
    }

}
