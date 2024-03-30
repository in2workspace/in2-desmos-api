//package es.in2.desmos.z.services.impl;
//
//import es.in2.desmos.z.domain.model.BrokerSubscription;
//import es.in2.desmos.z.services.BrokerSubscriptionService;
//import es.in2.desmos.services.broker.adapter.BrokerAdapterService;
//import es.in2.desmos.services.broker.adapter.factory.BrokerAdapterFactory;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//public class BrokerSubscriptionServiceImpl implements BrokerSubscriptionService {
//
//    private final BrokerAdapterService brokerAdapter;
//
//    public BrokerSubscriptionServiceImpl(BrokerAdapterFactory brokerAdapterFactory) {
//        this.brokerAdapter = brokerAdapterFactory.getBrokerAdapter();
//    }
//
//    @Override
//    public Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription) {
//        return brokerAdapter.createSubscription(processId, brokerSubscription);
//    }
//
//}
