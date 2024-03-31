package es.in2.desmos.workflows.impl;

import es.in2.desmos.workflows.DataSyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    @Override
    public Flux<Void> startDataSyncWorkflow(String processId) {
        // TODO: Add code here
        return Flux.empty();
    }

}
