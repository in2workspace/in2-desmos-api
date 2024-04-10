package es.in2.desmos.domain.services.sync.impl;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.services.sync.InternalEntitiesGetterService;
import reactor.core.publisher.Mono;

import java.util.List;

public class InternalEntitiesGetterServiceImpl implements InternalEntitiesGetterService {
    @Override
    public Mono<List<ProductOffering>> getInternalEntityIds() {
        // TODO
        return null;
    }
}
