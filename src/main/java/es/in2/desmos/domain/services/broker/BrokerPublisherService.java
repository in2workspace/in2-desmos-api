package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface BrokerPublisherService {

    Mono<Void> publishDataToBroker(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity);

    Mono<Void> publishNewBatchDataToBroker(String processId, List<MVEntity4DataNegotiation> mvEntity4DataNegotiationList, Map<Id, Entity> retrievedBrokerEntitiesList);
}
