package es.in2.desmos.domain.services.api.impl;

import es.in2.desmos.domain.models.BlockchainNotificationRecover;
import es.in2.desmos.domain.models.BlockchainTxPayloadRecover;
import es.in2.desmos.domain.repositories.PublishWorkflowRecoverRepository;
import es.in2.desmos.domain.repositories.SubscribeWorkflowRecoverRepository;
import es.in2.desmos.domain.services.api.RecoverRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecoverRepositoryServiceImpl implements RecoverRepositoryService {
    private final PublishWorkflowRecoverRepository publishWorkflowRecoverRepository;
    private final SubscribeWorkflowRecoverRepository subscribeWorkflowRecoverRepository;


    @Override
    public Mono<Void> saveBlockchainTxPayloadRecover(String processId, BlockchainTxPayloadRecover blockchainTxPayloadRecover) {
        log.debug("ProcessId: {} - Saving BlockchainTxPayloadRecover...", processId);
        return publishWorkflowRecoverRepository.save(blockchainTxPayloadRecover)
                .doOnSuccess(success -> log.info("ProcessId: {} - BlockchainTxPayloadRecover saved successfully", processId))
                .doOnError(error -> log.error("ProcessId: {} - Error saving BlockchainTxPayloadRecover: {}", processId, error.getMessage()))
                .then();
    }

    @Override
    public Mono<Void> saveBlockchainNotificationRecover(String processId, BlockchainNotificationRecover blockchainNotificationRecover) {
        log.debug("ProcessId: {} - Saving BlockchainNotificationRecover...", processId);
        return subscribeWorkflowRecoverRepository.save(blockchainNotificationRecover)
                .doOnSuccess(success -> log.info("ProcessId: {} - BlockchainNotificationRecover saved successfully", processId))
                .doOnError(error -> log.error("ProcessId: {} - Error saving BlockchainNotificationRecover: {}", processId, error.getMessage()))
                .then();
    }


}
