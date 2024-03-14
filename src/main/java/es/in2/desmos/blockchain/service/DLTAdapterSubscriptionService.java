package es.in2.desmos.blockchain.service;

import es.in2.desmos.blockchain.model.DLTAdapterSubscription;
import reactor.core.publisher.Mono;

public interface DLTAdapterSubscriptionService {
    Mono<Void> createSubscription(String processId, DLTAdapterSubscription dltAdapterSubscription);
}
