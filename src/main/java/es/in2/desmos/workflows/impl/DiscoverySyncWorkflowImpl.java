package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.events.EntitiesCreatorEventPublisher;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.services.broker.BrokerEntityIdGetterService;
import es.in2.desmos.workflows.DiscoverySyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscoverySyncWorkflowImpl implements DiscoverySyncWorkflow {
    private final BrokerEntityIdGetterService brokerEntityIdGetterService;
    private final EntitiesCreatorEventPublisher entitiesCreatorEventPublisher;

    @Override
    public Mono<List<Entity>> discoverySync(String processId, Mono<String> issuer, Mono<List<String>> externalEntityIds) {
        Mono<List<Entity>> internalProductOfferings = brokerEntityIdGetterService.getData();

        Mono<List<String>> internalEntityIds = internalProductOfferings.map(x -> x.stream().map(Entity::id).toList());
        entitiesCreatorEventPublisher.publishEvent(issuer, externalEntityIds, internalEntityIds);

        return internalProductOfferings;
    }
}
