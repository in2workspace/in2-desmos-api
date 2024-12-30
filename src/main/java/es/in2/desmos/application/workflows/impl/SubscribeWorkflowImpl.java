package es.in2.desmos.application.workflows.impl;

import es.in2.desmos.application.workflows.SubscribeWorkflow;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/*
 *  Workflow steps:
 *  1. Get the event from the SubscribeQueue.
 *  2. Retrieve the BrokerEntity from the external Broker.
 *  3. Verify the data integrity of the retrieved entity.
 *  4. Publish the retrieved entity to the local Broker.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeWorkflowImpl implements SubscribeWorkflow {

    private final QueueService pendingSubscribeEventsQueue;
    private final BrokerPublisherService brokerPublisherService;
    private final AuditRecordService auditRecordService;
    private final DataSyncService dataSyncService;

    @Override
    public Flux<Void> startSubscribeWorkflow(String processId) {
        log.info("ProcessID: {} - Starting the Subscribe Workflow...", processId);
        // Get the event stream for the events that need to be synchronized to the local broker
        return pendingSubscribeEventsQueue.getEventStream()
                // Get the next event (BlockchainNotification) from the queue
                .flatMap(pendingSubscribeQueueEventStream ->
                        Mono.just((BlockchainNotification) pendingSubscribeQueueEventStream.getEvent().get(0))
                                // verify that the DLTNotification is not null
                                .filter(Objects::nonNull)
                                // retrieve the entity from the source broker
                                .flatMap(blockchainNotification ->
                                        dataSyncService.getEntityFromExternalSource(processId, blockchainNotification)
                                                // verify the data integrity of the retrieved entity
                                                .flatMap(retrievedBrokerEntity ->
                                                        // Verify the integrity and consistency of the retrieved entity
                                                        {
                                                            System.out.println(" Retrieved broker entity: " + retrievedBrokerEntity);
                                                            return dataSyncService.verifyRetrievedEntityData(processId, blockchainNotification, retrievedBrokerEntity)
                                                                    // Build and save the audit record for RETRIEVED status
                                                                    .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                                                                    // Publish the retrieved entity to the local broker
                                                                    .then(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                                                                    // Build and save the audit record for PUBLISHED status
                                                                    .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.PUBLISHED));
                                                        }
                                                )
                                )
                                .doOnSuccess(success -> log.info("ProcessID: {} - Subscribe Workflow completed successfully.", processId))
                                .onErrorResume(error ->
                                        Mono.just(error)
                                                .doOnNext(errorObject ->
                                                        log.error("ProcessID: {} - Error occurred while processing the Subscribe Workflow: {}", processId, errorObject.getMessage()))
                                                .then(Mono.empty())));
    }

}