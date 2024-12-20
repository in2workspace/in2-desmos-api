package es.in2.desmos.domain.services.sync.services;

import es.in2.desmos.domain.models.BlockchainNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataSyncService {

    Mono<Void> synchronizeData(String processId);

    Flux<String> getEntityFromExternalSource(String processId, BlockchainNotification blockchainNotification);

    Mono<String> verifyRetrievedEntityData(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity);

}
