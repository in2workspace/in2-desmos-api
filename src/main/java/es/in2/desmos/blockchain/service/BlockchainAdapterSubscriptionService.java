package es.in2.desmos.blockchain.service;


import es.in2.desmos.blockchain.model.BlockchainAdapterSubscription;
import reactor.core.publisher.Mono;

public interface BlockchainAdapterSubscriptionService {

    Mono<Void> createSubscription(String processId, BlockchainAdapterSubscription blockchainAdapterSubscription);

}
