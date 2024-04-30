package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerPublisherService {

    Mono<Void> publishEntityToContextBroker(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity);

    Mono<List<MVBrokerEntity4DataNegotiation>> getMVBrokerEntities4DataNegotiation(String processId, String type, String firstAttribute, String secondAttribute);

    Mono<Void> batchUpsertEntitiesToContextBroker(String processId, String retrievedBrokerEntities);

    Mono<List<String>> findAllById(String processId, Mono<List<Id>> ids);
}
