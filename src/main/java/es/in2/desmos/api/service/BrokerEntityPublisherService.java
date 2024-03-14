package es.in2.desmos.api.service;

import es.in2.desmos.api.model.BlockchainNotification;
import reactor.core.publisher.Mono;

public interface BrokerEntityPublisherService {
    Mono<Void> publishRetrievedEntityToBroker(String processId, String retrievedEntity, BlockchainNotification blockchainNotification);
}
