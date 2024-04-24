package es.in2.desmos.domain.services.api;

import es.in2.desmos.domain.models.BlockchainNotificationRecover;
import es.in2.desmos.domain.models.BlockchainTxPayloadRecover;
import reactor.core.publisher.Mono;

public interface RecoverRepositoryService {
    Mono<Void> saveBlockchainTxPayloadRecover(String processId, BlockchainTxPayloadRecover blockchainTxPayloadRecover);
    Mono<Void> saveBlockchainNotificationRecover(String processId, BlockchainNotificationRecover blockchainNotificationRecover);

}
