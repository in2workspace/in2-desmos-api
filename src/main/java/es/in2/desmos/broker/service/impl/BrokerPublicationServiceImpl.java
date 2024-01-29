package es.in2.desmos.broker.service.impl;

import es.in2.desmos.broker.service.BrokerPublicationService;
import es.in2.desmos.broker.service.GenericBrokerService;
import es.in2.desmos.broker.util.BrokerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BrokerPublicationServiceImpl implements BrokerPublicationService {

    private final GenericBrokerService brokerAdapter;

    public BrokerPublicationServiceImpl(BrokerFactory brokerFactory) {
        this.brokerAdapter = brokerFactory.getBrokerAdapter();
    }

    public Mono<Void> postEntity(String processId, String requestBody) {
        return brokerAdapter.postEntity(processId, requestBody);
    }

    public Mono<String> getEntityById(String processId, String entityId) {
        return brokerAdapter.getEntityById(processId, entityId);
    }

    public Mono<Void> updateEntity(String processId, String requestBody) {
        return brokerAdapter.updateEntity(processId, requestBody);
    }

    public Mono<Void> deleteEntityById(String processId, String entityId) {
        return brokerAdapter.deleteEntityById(processId, entityId);
    }

}
