package es.in2.desmos.application.workflows.impl;

import es.in2.desmos.application.workflows.PublishWorkflow;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BrokerNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.blockchain.BlockchainPublisherService;
import es.in2.desmos.domain.utils.BlockchainTxPayloadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishWorkflowImpl implements PublishWorkflow {

    /*
     *  Workflow steps:
     *  1. Get the event from the PublisherQueue.
     *  2. Build the BlockchainTxPayload.
     *  3. Publish the BlockchainTxPayload to the Blockchain.
     */

    private final QueueService pendingPublishEventsQueue;
    private final AuditRecordService auditRecordService;
    private final BlockchainTxPayloadFactory blockchainTxPayloadFactory;
    private final BlockchainPublisherService blockchainPublisherService;

    @Override
    public Flux<Void> startPublishWorkflow(String processId) {
        log.info("ProcessID: {} - Starting the Publish Workflow...", processId);
        // Get the event stream from the data publication queue
        return pendingPublishEventsQueue.getEventStream()
                // Get the first event from the event stream,
                // parse it as a BrokerNotification and filter out null values
                .flatMap(pendingPublishQueueEventStream ->
                        Mono.just(pendingPublishQueueEventStream.getEvent().get(0))
                                .filter(BrokerNotification.class::isInstance)
                                .cast(BrokerNotification.class)
                                // Create an event from the BrokerNotification
                                .flatMap(brokerNotification -> Mono.just(brokerNotification.data())
                                        .flatMapIterable(currentBrokerNotificationDataList -> currentBrokerNotificationDataList)
                                        .flatMap(currentBrokerNotificationData ->
                                        {
                                            String id = currentBrokerNotificationData.get("id").toString();
                                            return auditRecordService.fetchLatestProducerEntityHashByEntityId(processId, id)
                                                    .switchIfEmpty(blockchainTxPayloadFactory.calculatePreviousHashIfEmpty(processId, currentBrokerNotificationData))
                                                    // Build the BlockchainTxPayload object
                                                    .flatMap(previousHash ->
                                                            blockchainTxPayloadFactory.buildBlockchainTxPayload(processId, currentBrokerNotificationData, previousHash))
                                                    // Save a new Audit Record with status CREATED
                                                    .flatMap(blockchainTxPayload ->
                                                            auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(processId, currentBrokerNotificationData, AuditRecordStatus.CREATED, blockchainTxPayload)
                                                                    // Publish the data event to the Blockchain
                                                                    .then(blockchainPublisherService.publishDataToBlockchain(processId, blockchainTxPayload))
                                                                    .then(auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(processId, currentBrokerNotificationData, AuditRecordStatus.PUBLISHED, blockchainTxPayload)));
                                        })
                                        .collectList()
                                        .then()
                                        .doOnSuccess(success ->
                                                log.info("ProcessID: {} - Publish Workflow completed successfully.", processId))
                                        .doOnError(error ->
                                                log.error("ProcessID: {} - Error occurred while processing the Publish Workflow: {}", processId, error.getMessage()))));
    }

}
