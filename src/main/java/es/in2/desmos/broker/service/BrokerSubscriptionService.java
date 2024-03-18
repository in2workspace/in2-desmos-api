package es.in2.desmos.broker.service;


import es.in2.desmos.broker.model.BrokerSubscription;
import reactor.core.publisher.Mono;

public interface BrokerSubscriptionService {
    Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription);
}
