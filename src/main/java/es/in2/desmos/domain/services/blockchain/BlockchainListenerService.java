package es.in2.desmos.domain.services.blockchain;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BlockchainSubscription;
import reactor.core.publisher.Mono;

public interface BlockchainListenerService {

    Mono<Void> createSubscription(String processId, BlockchainSubscription blockchainSubscription);

    Mono<Void> processBlockchainNotification(String processId, BlockchainNotification blockchainNotification);

}
