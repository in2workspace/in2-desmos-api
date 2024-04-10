package es.in2.desmos.domain.services.broker.impl;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerEntityIdGetterServiceImpl {
    private final BrokerAdapterService brokerAdapterService;

    public Mono<List<ProductOffering>> getData() {
        return brokerAdapterService.getEntityIds();
    }
}