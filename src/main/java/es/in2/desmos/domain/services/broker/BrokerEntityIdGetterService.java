package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.ProductOffering;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerEntityIdGetterService {
    Mono<List<ProductOffering>> getData();
}
