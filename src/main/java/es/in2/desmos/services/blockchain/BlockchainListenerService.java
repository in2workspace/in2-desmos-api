package es.in2.desmos.services.blockchain;

import es.in2.desmos.domain.model.BlockchainNotification;
import es.in2.desmos.domain.model.BlockchainSubscription;
import reactor.core.publisher.Mono;

public interface BlockchainListenerService {

    Mono<Void> createSubscription(String processId, BlockchainSubscription blockchainSubscription);

    Mono<Void> processBlockchainNotification(String processId, BlockchainNotification blockchainNotification);

}
