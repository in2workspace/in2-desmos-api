package es.in2.desmos.domain.services.blockchain.impl;

import es.in2.desmos.domain.exceptions.UnauthorizedDomeParticipantException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.repositories.DomeParticipantRepository;
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
    private final DomeParticipantRepository domeParticipantRepository;

    public BlockchainListenerServiceImpl(BlockchainAdapterFactory blockchainAdapterFactory, AuditRecordService auditRecordService,
                                         QueueService pendingSubscribeEventsQueue, DomeParticipantRepository domeParticipantRepository) {
        this.blockchainAdapterService = blockchainAdapterFactory.getBlockchainAdapter();
        this.auditRecordService = auditRecordService;
        this.pendingSubscribeEventsQueue = pendingSubscribeEventsQueue;
        this.domeParticipantRepository = domeParticipantRepository;
    }

    @Override
    public Mono<Void> createSubscription(String processId, BlockchainSubscription blockchainSubscription) {
        return blockchainAdapterService.createSubscription(processId, blockchainSubscription);
    }

    @Override
    public Mono<Void> processBlockchainNotification(String processId, BlockchainNotification blockchainNotification) {
        // Create and AuditRecord with status RECEIVED
        return domeParticipantRepository.findByEthereumAddress(blockchainNotification.ethereumAddress())
                .flatMap(domeParticipant -> {
                    if(domeParticipant!=null) {
                        return auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification,
                                        null, AuditRecordStatus.RECEIVED)
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
                    } else {
                        return Mono.error(new UnauthorizedDomeParticipantException("Unauthorized DOME Participant"));
                    }
                });
    }

}
