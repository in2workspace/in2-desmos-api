package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.services.sync.NewEntitiesCreatorService;
import es.in2.desmos.workflows.DiscoverySyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscoverySyncWorkflowImpl implements DiscoverySyncWorkflow {
    private final NewEntitiesCreatorService newEntitiesCreatorService;

    @Override
    public List<ProductOffering> discoverySync(String processId, String issuer, List<String> externalEntityIds) {
        newEntitiesCreatorService.addNewEntities(externalEntityIds, issuer);
        return getInternalEntityIds();
    }


}
