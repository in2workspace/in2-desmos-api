package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerEntityGetterService {
    Mono<List<MVEntity4DataNegotiation>> getMvEntities4DataNegotiation();
}
