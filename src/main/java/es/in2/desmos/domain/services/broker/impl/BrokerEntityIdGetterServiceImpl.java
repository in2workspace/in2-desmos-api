package es.in2.desmos.domain.services.broker.impl;

import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.services.broker.BrokerEntityIdGetterService;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class BrokerEntityIdGetterServiceImpl implements BrokerEntityIdGetterService {
    private final BrokerAdapterService brokerAdapterService;

    public BrokerEntityIdGetterServiceImpl(BrokerAdapterFactory brokerAdapterFactory) {
        this.brokerAdapterService = brokerAdapterFactory.getBrokerAdapter();
    }

    @Override
    public Mono<List<Entity>> getData() {
        return brokerAdapterService.getEntityIds();
    }
}