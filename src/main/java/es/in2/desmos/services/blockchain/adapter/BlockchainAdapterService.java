package es.in2.desmos.services.blockchain.adapter;

import es.in2.desmos.domain.model.BlockchainSubscription;
import es.in2.desmos.domain.model.BlockchainData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlockchainAdapterService {
    Mono<Void> createSubscription(String processId, BlockchainSubscription blockchainSubscription);
    Mono<Void> publishEvent(String processId, BlockchainData blockchainData);
    Flux<String> getEventsFromRange(String processId, long from, long to);
}

