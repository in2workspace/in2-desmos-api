package es.in2.desmos.domain.services.blockchain.impl;

import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.domain.services.blockchain.adapter.factory.BlockchainAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Service
public class BlockchainListenerServiceImpl implements BlockchainListenerService {

    private final BlockchainAdapterService blockchainAdapterService;
    private final AuditRecordService auditRecordService;
    private final QueueService pendingSubscribeEventsQueue;

    public BlockchainListenerServiceImpl(BlockchainAdapterFactory blockchainAdapterFactory, AuditRecordService auditRecordService, QueueService pendingSubscribeEventsQueue) {
        this.blockchainAdapterService = blockchainAdapterFactory.getBlockchainAdapter();
        this.auditRecordService = auditRecordService;
        this.pendingSubscribeEventsQueue = pendingSubscribeEventsQueue;
    }

    @Override
    public Mono<Void> createSubscription(String processId, BlockchainSubscription blockchainSubscription) {
        return blockchainAdapterService.createSubscription(processId, blockchainSubscription);
    }

    @Override
    public Mono<Void> processBlockchainNotification(String processId, BlockchainNotification blockchainNotification) {
        // Ensure that BlockchainNotification is not null
        return validateDLTNotification(blockchainNotification)
                // Create and AuditRecord with status RECEIVED
                .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification,
                        null, AuditRecordStatus.RECEIVED))
                // Set priority for the pendingSubscribeEventsQueue event
                .then(Mono.just(EventQueuePriority.MEDIUM))
                // Enqueue DLTNotification to DataRetrievalQueue
                .flatMap(eventQueuePriority ->
                {
                    log.debug("ProcessID: {} - Enqueuing Blockchain Notification to DataRetrievalQueue...", processId);
                    return pendingSubscribeEventsQueue.enqueueEvent(EventQueue.builder()
                            .event(Collections.singletonList(blockchainNotification))
                            .priority(eventQueuePriority)
                            .build());
                });
    }

    // todo: this method will be removed in the future when validation is implemented
    private Mono<Void> validateDLTNotification(BlockchainNotification blockchainNotification) {
        checkIfNotificationIsNullOrDataLocationIsEmpty(blockchainNotification);
        return Mono.empty();
    }

    // todo: this method will be removed in the future when validation is implemented
    private void checkIfNotificationIsNullOrDataLocationIsEmpty(BlockchainNotification blockchainNotification) {
        if (blockchainNotification == null || blockchainNotification.dataLocation().isEmpty()) {
            throw new IllegalArgumentException("Invalid Blockchain Notification");
        }
    }

}
