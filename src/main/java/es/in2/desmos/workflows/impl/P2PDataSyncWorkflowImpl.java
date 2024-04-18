package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.DataNegotiationEvent;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.BrokerEntityGetterService;
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
    private final BrokerEntityGetterService brokerEntityGetterService;
    private final DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Override
    public Mono<List<MVEntity4DataNegotiation>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiation) {
        Mono<List<MVEntity4DataNegotiation>> localMvEntities4DataNegotiation = brokerEntityGetterService.getMvEntities4DataNegotiation();

        DataNegotiationEvent dataNegotiationEvent = new DataNegotiationEvent(processId, issuer, externalMvEntities4DataNegotiation, localMvEntities4DataNegotiation);
        dataNegotiationEventPublisher.publishEvent(dataNegotiationEvent);

        return localMvEntities4DataNegotiation;
    }
}
