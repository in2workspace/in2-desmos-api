package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BrokerEntityWithIdAndType;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.models.Id;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerPublisherService {

    Mono<Void> publishDataToBroker(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity);

    <T extends BrokerEntityWithIdAndType> Mono<List<T>> findAllIdTypeAndAttributesByType(String processId, String type, String firstAttribute, String secondAttribute, String thirdAttribute, String forthAttribute, Class<T[]> responseClassArray);

    Mono<List<Entity>> findEntitiesAndItsSubentitiesByIdInBase64(String processId, Mono<List<Id>> idsMono, List<Id> processedEntities);

    Mono<String> getEntityById(String processId, String entityId);

    Mono<Void> postEntity(String processId, String requestBody);
}