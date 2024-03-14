package es.in2.desmos.api.facade;

import es.in2.desmos.api.model.BrokerNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BrokerToBlockchainPublisher {
    Flux<Void> startProcessingEvents();
    Mono<Void> processAndPublishBrokerNotificationToBlockchain(String processId, BrokerNotification brokerNotification);
}
