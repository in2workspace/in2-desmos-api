package es.in2.desmos.domain.services.broker.impl;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static es.in2.desmos.domain.utils.ApplicationUtils.extractEntityIdFromDataLocation;

@Slf4j
@Service
public class BrokerPublisherServiceImpl implements BrokerPublisherService {

    private final BrokerAdapterService brokerAdapterService;

    public BrokerPublisherServiceImpl(BrokerAdapterFactory brokerAdapterFactory) {
        this.brokerAdapterService = brokerAdapterFactory.getBrokerAdapter();
    }

    @Override
    public Mono<Void> publishDataToBroker(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity) {
        // Get the entity ID from the data location in the blockchain notification.
        // This is used to check if the retrieved entity exists in the local broker or not.
        // If it exists, the entity will be updated, otherwise, it will be created.
        String entityId = extractEntityIdFromDataLocation(blockchainNotification.dataLocation());
        return getEntityById(processId, entityId)
                .switchIfEmpty(Mono.just(""))
                .flatMap(response -> {
                    if(response.isBlank()) {
                        log.info("ProcessID: {} - Entity doesn't exist", processId);
                        // Logic for when the entity does not exist, for example, creating it
                        return postEntity(processId, retrievedBrokerEntity);
                    } else {
                        // Logic for when the entity exists
                        log.info("ProcessId: {} - Entity exists", processId);
                        return updateEntity(processId, retrievedBrokerEntity);
                    }
                });
    }

    @Override
    public Mono<Void> publishNewBatchDataToBroker(String processId, List<MVEntity4DataNegotiation> mvEntity4DataNegotiationList, Map<Id, Entity> retrievedBrokerEntitiesList) {
        return null;
    }

    private Mono<Void> postEntity(String processId, String requestBody) {
        return brokerAdapterService.postEntity(processId, requestBody);
    }

    private Mono<String> getEntityById(String processId, String entityId) {
        return brokerAdapterService.getEntityById(processId, entityId);
    }

    private Mono<Void> updateEntity(String processId, String requestBody) {
        return brokerAdapterService.updateEntity(processId, requestBody);
    }

}
