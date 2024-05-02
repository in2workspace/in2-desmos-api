package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.workflows.DataSyncWorkflow;
import es.in2.desmos.workflows.jobs.P2PDataSyncJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncWorkflowImpl implements DataSyncWorkflow {

    /*
     *  Workflow steps:
     *  1. Get all ProductOffering ID entities from the local Broker.
     *  2. Send a POST request to the configured external Broker with the ProductOffering ID entity list.
     *  3. Compare the response with the list of ProductOffering ID entities from the local Broker.
     *  4. If the response contains new ProductOffering ID entities,
     *  send a request to the external Broker to get the new ProductOfferings and its related entities.
     *  5. Publish the new ProductOfferings and its related entities to the local Broker.
     */

    private final P2PDataSyncJob p2PDataSyncJob;

    @Override
    public Flux<Void> startDataSyncWorkflow(String processId) {
        // TODO: Add code here
        return Flux.empty();
    }

    @Override
    public Mono<Void> synchronizeData(String processId) {
        log.debug("ProcessID: {} - Synchronizing data...", processId);
        return p2PDataSyncJob.synchronizeData(processId);
    }

    @Override
    public Mono<List<MVEntity4DataNegotiation>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiation) {
        return p2PDataSyncJob.dataDiscovery(processId, issuer, externalMvEntities4DataNegotiation);
    }

    @Override
    public Mono<List<String>> getLocalEntitiesById(String processId, Mono<List<Id>> ids) {
        return p2PDataSyncJob.getLocalEntitiesById(processId, ids);
    }

}
