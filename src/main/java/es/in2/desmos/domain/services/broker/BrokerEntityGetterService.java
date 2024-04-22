package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerEntityGetterService {
    Mono<List<MVBrokerEntity4DataNegotiation>> getMvBrokerEntities4DataNegotiation(String processId);
}
