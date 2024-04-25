package es.in2.desmos.domain.services.broker.adapter;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BrokerSubscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerAdapterService {
    Mono<Void> postEntity(String processId, String requestBody, BlockchainNotification blockchainNotification);
    Flux<String> getEntitiesByTimeRange(String processId, String timestamp);
    Mono<String> getEntityById(String processId, String entityId, BlockchainNotification blockchainNotification);
    Mono<Void> updateEntity(String processId, String requestBody, BlockchainNotification blockchainNotification);
    Mono<Void> deleteEntityById(String processId, String entityId);
    Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription);
    Mono<List<BrokerSubscription>> getSubscriptions(String processId);
    Mono<Void> updateSubscription(String processId, BrokerSubscription brokerSubscription);
    Mono<Void> deleteSubscription(String processId, String subscriptionId);
}

