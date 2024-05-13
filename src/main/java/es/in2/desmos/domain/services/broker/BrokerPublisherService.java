package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BrokerEntityWithIdAndType;
import es.in2.desmos.domain.models.Id;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerPublisherService {

    Mono<Void> publishDataToBroker(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity);

    <T extends BrokerEntityWithIdAndType> Mono<List<T>> findAllIdTypeFirstAttributeAndSecondAttributeByType(String processId, String type, String firstAttribute, String secondAttribute, Class<T[]> responseClassArray, Class<T> responseClass);

    Mono<Void> batchUpsertEntitiesToContextBroker(String processId, String retrievedBrokerEntities);

    Mono<List<String>> findAllById(String processId, Mono<List<Id>> ids);
}