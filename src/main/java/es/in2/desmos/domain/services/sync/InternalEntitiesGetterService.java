package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.ProductOffering;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InternalEntitiesGetterService {
    Mono<List<ProductOffering>> getInternalEntities();
}
