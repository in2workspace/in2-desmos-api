package es.in2.desmos.services.blockchain;

import es.in2.desmos.domain.model.BlockchainData;
import es.in2.desmos.domain.model.BlockchainNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlockchainPublisherService {

    Flux<Void> publishDataToBlockchain();

}
