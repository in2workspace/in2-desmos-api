package es.in2.desmos.blockchain.service;


import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.blockchain.model.BlockchainNode;
import es.in2.desmos.blockchain.model.BlockchainAdapterSubscription;
import reactor.core.publisher.Mono;

public interface GenericBlockchainAdapterService {

    Mono<String> setNodeConnection(String processId, BlockchainNode blockchainNode);

    Mono<Void> createSubscription(String processId, BlockchainAdapterSubscription blockchainAdapterSubscription);

    Mono<Void> publishEvent(String processId, BlockchainEvent blockchainEvent);

}

