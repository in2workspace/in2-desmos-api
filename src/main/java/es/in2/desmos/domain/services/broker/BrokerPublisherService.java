package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.BlockchainNotification;
import reactor.core.publisher.Mono;

public interface BrokerPublisherService {

    Mono<Void> PublishEntityToContextBroker(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity);

}