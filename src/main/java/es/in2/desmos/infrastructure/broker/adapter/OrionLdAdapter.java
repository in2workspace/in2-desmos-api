package es.in2.desmos.infrastructure.broker.adapter;

import es.in2.desmos.infrastructure.broker.model.BrokerSubscription;
import es.in2.desmos.infrastructure.broker.service.GenericBrokerService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class OrionLdAdapter implements GenericBrokerService {

    @Override
    public Mono<Void> postEntity(String processId, String requestBody) {
        return null;
    }

    @Override
    public Flux<String> getEntitiesByTimeRange(String processId, String timestamp) {return null;}

    @Override
    public Mono<String> getEntityById(String processId, String entityId) {
        return null;
    }

    @Override
    public Mono<Void> updateEntity(String processId, String requestBody) {
        return null;
    }

    @Override
    public Mono<Void> deleteEntityById(String processId, String entityId) {
        return null;
    }

    @Override
    public Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription) {
        return null;
    }

    @Override
    public Mono<List<BrokerSubscription>> getSubscriptions(String processId) {
        return null;
    }

    @Override
    public Mono<Void> updateSubscription(String processId, BrokerSubscription brokerSubscription) {
        return null;
    }

    @Override
    public Mono<Void> deleteSubscription(String processId, String subscriptionId) {
        return null;
    }

}
