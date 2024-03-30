//package es.in2.desmos.z.services.impl;
//
//import es.in2.desmos.z.services.BrokerPublicationService;
//import es.in2.desmos.services.broker.adapter.BrokerAdapterService;
//import es.in2.desmos.services.broker.adapter.factory.BrokerAdapterFactory;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Service
//public class BrokerPublicationServiceImpl implements BrokerPublicationService {
//
//    private final BrokerAdapterService brokerAdapter;
//
//    public BrokerPublicationServiceImpl(BrokerAdapterFactory brokerAdapterFactory) {
//        this.brokerAdapter = brokerAdapterFactory.getBrokerAdapter();
//    }
//
//    public Flux<String> getEntitiesByTimeRange(String processId, String timestamp) {
//        return brokerAdapter.getEntitiesByTimeRange(processId, timestamp);
//    }
//
//    public Mono<Void> postEntity(String processId, String requestBody) {
//        return brokerAdapter.postEntity(processId, requestBody);
//    }
//
//    public Mono<String> getEntityById(String processId, String entityId) {
//        return brokerAdapter.getEntityById(processId, entityId);
//    }
//
//    public Mono<Void> updateEntity(String processId, String requestBody) {
//        return brokerAdapter.updateEntity(processId, requestBody);
//    }
//
//    public Mono<Void> deleteEntityById(String processId, String entityId) {
//        return brokerAdapter.deleteEntityById(processId, entityId);
//    }
//
//}
