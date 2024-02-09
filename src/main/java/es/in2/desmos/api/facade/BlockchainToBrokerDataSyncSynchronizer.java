package es.in2.desmos.api.facade;

import es.in2.desmos.api.model.BlockchainNotification;
import reactor.core.publisher.Mono;

public interface BlockchainToBrokerDataSyncSynchronizer {
    Mono<Void> retrieveAndSynchronizeEntityIntoBroker(String processId, BlockchainNotification blockchainNotification);
}
