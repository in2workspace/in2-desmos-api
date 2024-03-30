package es.in2.desmos.services.blockchain.impl;

import es.in2.desmos.domain.model.BlockchainNotification;
import es.in2.desmos.domain.model.BlockchainSubscription;
import es.in2.desmos.domain.model.EventQueuePriority;
import es.in2.desmos.domain.service.AuditRecordService;
import es.in2.desmos.domain.service.QueueService;
import es.in2.desmos.services.blockchain.BlockchainListenerService;
import es.in2.desmos.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.services.blockchain.adapter.factory.BlockchainAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static es.in2.desmos.domain.util.ApplicationUtils.checkIfHashLinkExistInDataLocation;

@Slf4j
@Service
public class BlockchainListenerServiceImpl implements BlockchainListenerService {

    private final BlockchainAdapterService blockchainAdapterService;
    private final AuditRecordService auditRecordService;
    private final QueueService dataRetrievalQueue;

    public BlockchainListenerServiceImpl(BlockchainAdapterFactory blockchainAdapterFactory, AuditRecordService auditRecordService, QueueService dataRetrievalQueue) {
        this.blockchainAdapterService = blockchainAdapterFactory.getBlockchainAdapter();
        this.auditRecordService = auditRecordService;
        this.dataRetrievalQueue = dataRetrievalQueue;
    }

    @Override
    public Mono<Void> createSubscription(String processId, BlockchainSubscription blockchainSubscription) {
        return blockchainAdapterService.createSubscription(processId, blockchainSubscription);
    }

    @Override
    public Mono<Void> processBlockchainNotification(String processId, BlockchainNotification blockchainNotification) {
        // Validate DLTNotification is not null
        return validateDLTNotification(blockchainNotification)
                // Save AuditRecord
                // fixme:
//                .then(auditRecordService.buildAndSaveAuditRecord(processId, )
                // Set priority for DLTNotification
//                .then(setPriorityForDLTNotification(blockchainNotification))
                // Enqueue DLTNotification to DataRetrievalQueue
                // fixme:
//                .flatMap(eventQueuePriority -> dataRetrievalQueue.enqueueEvent(EventQueue.builder().event(Collections.singletonList(blockchainNotification)).priority(eventQueuePriority).build());
                ;
    }

    private Mono<Void> validateDLTNotification(BlockchainNotification blockchainNotification) {
        checkIfNotificationIsNullOrDataLocationIsEmpty(blockchainNotification);
        return Mono.empty();
    }

    private void checkIfNotificationIsNullOrDataLocationIsEmpty(BlockchainNotification blockchainNotification) {
        if (blockchainNotification == null || blockchainNotification.dataLocation().isEmpty()) {
            throw new IllegalArgumentException("Invalid Blockchain Notification");
        }
    }

    // TODO: Review this method -> check if all decisions are being made correctly
    private Mono<EventQueuePriority> setPriorityForDLTNotification(BlockchainNotification blockchainNotification) {
        EventQueuePriority eventQueuePriority = EventQueuePriority.PUBLICATION_PUBLISH;
        if (!checkIfHashLinkExistInDataLocation(blockchainNotification.dataLocation())) {
            eventQueuePriority = EventQueuePriority.PUBLICATION_DELETE;
        } else if (!Objects.equals(blockchainNotification.previousEntityHash(), "0x0000000000000000000000000000000000000000000000000000000000000000")) {
            eventQueuePriority = EventQueuePriority.PUBLICATION_EDIT;
        }
        return Mono.just(eventQueuePriority);
    }

}
