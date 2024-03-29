package es.in2.desmos.infrastructure.integration;

import es.in2.desmos.application.service.DataPublicationService;
import es.in2.desmos.application.service.DataRetrievalService;
import es.in2.desmos.application.service.DataSyncService;
import es.in2.desmos.domain.service.AuditRecordService;
import jakarta.annotation.PreDestroy;
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

    private final AuditRecordService auditRecordService;
    private final DataSyncService dataSyncService;
    private final DataPublicationService dataPublicationService;
    private final DataRetrievalService dataRetrievalService;

    private Disposable blockchainEventProcessingSubscription;
    private Disposable brokerEntityEventProcessingSubscription;

    /**
     * Flag to control if the queues can emit messages.
     * This is used to avoid emitting messages before the initial synchronization is finished.
     **/
    private final AtomicBoolean isQueueAuthorizedForEmit = new AtomicBoolean(false);

    @EventListener(ApplicationReadyEvent.class)
    public Mono<Void> initializeDataSync() {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Initializing Data Synchronization...", processId);
        // Check if there are any previous audit records
        return auditRecordService.getAllAuditRecords(processId).collectList()
                .flatMap(auditRecords -> {
                    // If there are no previous audit records, start the synchronization
                    if (auditRecords.isEmpty()) {
                        log.info("ProcessID: {} - No previous audit records found, starting synchronization...", processId);
                        return dataSyncService.synchronizeData(processId);
                    }
                    // If there are previous audit records, skip the synchronization
                    else {
                        log.info("ProcessID: {} - Previous audit records found, skipping synchronization...", processId);
                        return Mono.empty();
                    }
                })
                // Enable queue processing after the synchronization is finished
                .doOnTerminate(() -> {
                    log.info("Data Synchronization finished, enabling Queue Processing...");
                    isQueueAuthorizedForEmit.set(true);
                    initializeQueueProcessing();
                    log.info("Queue Processor has been enabled.");
                });
    }

    // TODO: Check why we need to use the subscribe method here
    private void initializeQueueProcessing() {
        if (isQueueAuthorizedForEmit.get()) {
            log.debug("Starting queue processing...");
            disposeIfActive(blockchainEventProcessingSubscription);
            disposeIfActive(brokerEntityEventProcessingSubscription);
            blockchainEventProcessingSubscription = dataPublicationService.startPublishingDataToDLT()
                    .subscribe(
                            null,
                            error -> log.error("Error occurred during blockchain event processing"),
                            () -> log.info("Blockchain event processing completed")
                    );
            brokerEntityEventProcessingSubscription = dataRetrievalService.startRetrievingData()
                    .subscribe(
                            null,
                            error -> log.error("Error occurred during broker entity event processing"),
                            () -> log.info("Broker entity event processing completed")
                    );
        } else {
            log.debug("Queue processing is currently paused.");
        }
    }

    private void disposeIfActive(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    @PreDestroy
    public void cleanUp() {
        disposeIfActive(blockchainEventProcessingSubscription);
        disposeIfActive(brokerEntityEventProcessingSubscription);
    }

}
