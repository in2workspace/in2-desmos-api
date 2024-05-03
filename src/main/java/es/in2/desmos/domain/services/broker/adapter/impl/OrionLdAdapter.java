package es.in2.desmos.domain.services.broker.adapter.impl;

import es.in2.desmos.domain.models.BrokerSubscription;
import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// TODO: Implement OrionLdAdapter
@Component
public class OrionLdAdapter implements BrokerAdapterService {

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

    @Override
    public Mono<List<MVBrokerEntity4DataNegotiation>> getMVBrokerEntities4DataNegotiation(String processId, String type, String firstAttribute, String secondAttribute) { return null; }

    @Override
    public Mono<Void> batchUpsertEntities(String processId, String requestBody) { return null; }

}
