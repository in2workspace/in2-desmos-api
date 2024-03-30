package es.in2.desmos.services.blockchain;

import reactor.core.publisher.Flux;

public interface BlockchainPublisherService {

    Flux<Void> publishDataToBlockchain();

}
