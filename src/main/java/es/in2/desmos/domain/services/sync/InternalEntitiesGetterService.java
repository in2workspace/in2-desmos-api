package es.in2.desmos.domain.services.sync;

import reactor.core.publisher.Mono;

import java.util.List;

public interface InternalEntitiesGetterService {
    Mono<List<String>> getInternalEntities();
}
