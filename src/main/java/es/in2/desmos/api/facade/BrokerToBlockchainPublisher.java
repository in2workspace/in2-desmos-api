package es.in2.desmos.api.facade;

import es.in2.desmos.api.model.BrokerNotification;
import reactor.core.publisher.Mono;

public interface BrokerToBlockchainPublisher {

    Mono<Void> processAndPublishBrokerNotificationToBlockchain(String processId, BrokerNotification brokerNotification);

}
