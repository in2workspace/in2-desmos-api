package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.services.sync.InternalEntitiesGetterService;
import es.in2.desmos.domain.services.sync.NewEntitiesCreatorService;
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
    private final InternalEntitiesGetterService internalEntitiesGetterService;
    private final NewEntitiesCreatorService newEntitiesCreatorService;

    @Override
    public Mono<List<ProductOffering>> discoverySync(String processId, Mono<String> issuer, Mono<List<String>> externalEntityIds) {
        Mono<List<String>> internalEntityIds = internalEntitiesGetterService.getInternalEntities();
        var newEntitiesAdder = newEntitiesCreatorService.addNewEntities(issuer, externalEntityIds, internalEntityIds);

        // TODO
        // return getInternalEntityIds();
        return null;
    }
}
