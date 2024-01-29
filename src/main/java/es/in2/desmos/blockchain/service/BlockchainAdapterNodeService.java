package es.in2.desmos.blockchain.service;


import es.in2.desmos.blockchain.model.BlockchainNode;
import reactor.core.publisher.Mono;

public interface BlockchainAdapterNodeService {

    Mono<String> createBlockchainNodeConnection(String processId, BlockchainNode blockchainNode);

}
