package es.in2.desmos.domain.services.api;

import reactor.core.publisher.Mono;


public interface BrokerSubscriptionValidateService {

    Mono<Void> setSubscriptionId(String processId, String id);

    Mono<Void> validateSubscription(String processId, String id);
}
