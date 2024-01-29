package es.in2.desmos.broker.service.impl;

import es.in2.desmos.broker.model.BrokerSubscription;
import es.in2.desmos.broker.service.BrokerSubscriptionService;
import es.in2.desmos.broker.service.GenericBrokerService;
import es.in2.desmos.broker.util.BrokerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BrokerSubscriptionServiceImpl implements BrokerSubscriptionService {

    private final GenericBrokerService brokerAdapter;

    public BrokerSubscriptionServiceImpl(BrokerFactory brokerFactory) {
        this.brokerAdapter = brokerFactory.getBrokerAdapter();
    }

    @Override
    public Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription) {
        return brokerAdapter.createSubscription(processId, brokerSubscription);
    }

    @Override
    public Mono<List<BrokerSubscription>> getSubscriptions(String processId) {
        return brokerAdapter.getSubscriptions(processId);
    }

    @Override
    public Mono<Void> updateSubscription(String processId, BrokerSubscription brokerSubscription) {
        return brokerAdapter.updateSubscription(processId, brokerSubscription);
    }

    @Override
    public Mono<Void> deleteSubscription(String processId, String subscriptionId) {
        return brokerAdapter.deleteSubscription(processId, subscriptionId);
    }

}
