package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.DataNegotiationEvent;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.services.broker.BrokerEntityIdGetterService;
import es.in2.desmos.workflows.P2PDataSyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class P2PDataSyncWorkflowImpl implements P2PDataSyncWorkflow {
    private final BrokerEntityIdGetterService brokerEntityIdGetterService;
    private final DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Override
    public Mono<List<Entity>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<Entity>> externalEntities) {
        Mono<List<Entity>> internalEntities = brokerEntityIdGetterService.getData();

        DataNegotiationEvent dataNegotiationEvent = new DataNegotiationEvent(issuer, externalEntities, internalEntities);
        dataNegotiationEventPublisher.publishEvent(dataNegotiationEvent);

        return internalEntities;
    }
}
