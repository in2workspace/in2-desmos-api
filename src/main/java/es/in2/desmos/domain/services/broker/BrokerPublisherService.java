package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.BlockchainNotification;
import reactor.core.publisher.Mono;

public interface BrokerPublisherService {

    Mono<Void> publishEntityToContextBroker(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity);

    Mono<Void> batchUpsertEntitiesToContextBroker(String processId, String retrievedBrokerEntities);
}
