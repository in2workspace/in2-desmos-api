package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.BrokerEntityRetrievalService;
import es.in2.desmos.domain.services.api.BrokerEntityVerifyService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.workflows.SubscribeWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeWorkflowImpl implements SubscribeWorkflow {

    /*
     *  Workflow steps:
     *  1. Get the event from the SubscribeQueue.
     *  2. Retrieve the BrokerEntity from the external Broker.
     *  3. Publish the retrieved entity to the local Broker.
     */

    private final QueueService pendingSubscribeEventsQueue;
    private final BrokerPublisherService brokerPublisherService;
    private final AuditRecordService auditRecordService;
    private final BrokerEntityRetrievalService brokerEntityRetrievalService;
    private final BrokerEntityVerifyService brokerEntityVerifyService;

    @Override
    public Flux<Void> startSubscribeWorkflow() {
        log.info("Starting the Subscribe Workflow...");
        // Generate a processId
        String processId = UUID.randomUUID().toString();
        log.debug("Process of retrieving data from the external sources started with processID: {}", processId);
        // The dataRetrievalQueue is a QueueService object used to retrieve data from the external sources
        return pendingSubscribeEventsQueue.getEventStream()
                // get the next BlockchainNotification from the queue
                .flatMap(pendingSubscribeQueueEventStream ->
                        Mono.just((BlockchainNotification) pendingSubscribeQueueEventStream.getEvent().get(0))
                                // verify that the DLTNotification is not null
                                .filter(Objects::nonNull)
                                // retrieve the entity from the source broker
                                .flatMap(blockchainNotification ->
                                        brokerEntityRetrievalService.retrieveEntityFromExternalBroker(processId, blockchainNotification)
                                                // verify the data integrity of the retrieved entity
                                                .flatMap(retrievedBrokerEntity ->
                                                        brokerEntityVerifyService.verifyRetrievedEntityDataIntegrity(processId, blockchainNotification, retrievedBrokerEntity)
                                                                // build and save the audit record for RETRIEVED status
                                                                        .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                                                                // publish the retrieved entity to the local broker
                                                                .then(brokerPublisherService.publishEntityToContextBroker(processId, blockchainNotification, retrievedBrokerEntity))
                                                                // build and save the audit record for PUBLISHED status
                                                                .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.PUBLISHED))
                                                )
                                )
                                .doOnSuccess(success -> log.info("ProcessID: {} - Subscribe Workflow completed successfully.", processId))
                                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the Subscribe Workflow: {}", processId, error.getMessage())));
    }


}
