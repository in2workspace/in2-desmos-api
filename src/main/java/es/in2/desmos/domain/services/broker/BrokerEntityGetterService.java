package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.Entity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerEntityGetterService {
    Mono<List<Entity>> getBasicData();
}
