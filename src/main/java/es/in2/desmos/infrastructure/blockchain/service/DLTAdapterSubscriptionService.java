package es.in2.desmos.infrastructure.blockchain.service;

import es.in2.desmos.infrastructure.blockchain.model.DLTAdapterSubscription;
import reactor.core.publisher.Mono;

public interface DLTAdapterSubscriptionService {
    Mono<Void> createSubscription(String processId, DLTAdapterSubscription dltAdapterSubscription);
}
