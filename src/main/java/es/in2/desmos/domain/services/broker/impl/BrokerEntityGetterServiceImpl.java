package es.in2.desmos.domain.services.broker.impl;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.BrokerEntityGetterService;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class BrokerEntityGetterServiceImpl implements BrokerEntityGetterService {
    private final BrokerAdapterService brokerAdapterService;

    public BrokerEntityGetterServiceImpl(BrokerAdapterFactory brokerAdapterFactory) {
        this.brokerAdapterService = brokerAdapterFactory.getBrokerAdapter();
    }

    @Override
    public Mono<List<MVEntity4DataNegotiation>> getMvEntities4DataNegotiation() {
        return brokerAdapterService.getMvEntities4DataNegotiation();
    }
}