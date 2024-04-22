package es.in2.desmos.workflows.impl;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.DataNegotiationEvent;
import es.in2.desmos.domain.models.MVAuditServiceEntity4DataNegotiation;
import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerEntityGetterService;
import es.in2.desmos.workflows.P2PDataSyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class P2PDataSyncWorkflowImpl implements P2PDataSyncWorkflow {
    private final BrokerEntityGetterService brokerEntityGetterService;

    private final AuditRecordService auditRecordService;

    private final DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Override
    public Mono<List<MVEntity4DataNegotiation>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiation) {
        log.info("ProcessID: {} - Starting the P2P Data Sync Workflow", processId);

        Mono<List<MVBrokerEntity4DataNegotiation>> mvBrokerEntities4DataNegotiationMono = brokerEntityGetterService.getMvBrokerEntities4DataNegotiation(processId);

        Mono<List<String>> entities4DataNegotiationIds = mvBrokerEntities4DataNegotiationMono.map(x -> x.stream().map(MVBrokerEntity4DataNegotiation::id).toList());
        Mono<List<MVAuditServiceEntity4DataNegotiation>> mvAuditServiceEntities4DataNegotiationMono = getMvAuditServiceEntity4DataNegotiation(processId, entities4DataNegotiationIds);

        Mono<List<MVEntity4DataNegotiation>> localMvEntities4DataNegotiation = createLocalMvEntities4DataNegotiation(mvBrokerEntities4DataNegotiationMono, mvAuditServiceEntities4DataNegotiationMono);

        DataNegotiationEvent dataNegotiationEvent = new DataNegotiationEvent(processId, issuer, externalMvEntities4DataNegotiation, localMvEntities4DataNegotiation);
        dataNegotiationEventPublisher.publishEvent(dataNegotiationEvent);

        log.info("ProcessID: {} - Finishing the P2P Data Sync Workflow", processId);
        return localMvEntities4DataNegotiation;
    }

    private Mono<List<MVAuditServiceEntity4DataNegotiation>> getMvAuditServiceEntity4DataNegotiation(String processId, Mono<List<String>> entities4DataNegotiationIdsMono) {
        return entities4DataNegotiationIdsMono.flatMap(entities4DataNegotiationIds ->
                Flux.fromIterable(entities4DataNegotiationIds)
                        .flatMap(id -> auditRecordService.findLatestAuditRecordForEntity(processId, id)
                                .map(auditRecord -> new MVAuditServiceEntity4DataNegotiation(
                                                auditRecord.getEntityId(),
                                                auditRecord.getEntityHash(),
                                                auditRecord.getEntityHashLink()
                                        )
                                )
                        )
                        .collectList()
        );
    }

    private Mono<List<MVEntity4DataNegotiation>> createLocalMvEntities4DataNegotiation(
            Mono<List<MVBrokerEntity4DataNegotiation>> mvBrokerEntities4DataNegotiationMono,
            Mono<List<MVAuditServiceEntity4DataNegotiation>> mvAuditServiceEntities4DataNegotiationMono) {
        return Mono.zip(mvBrokerEntities4DataNegotiationMono, mvAuditServiceEntities4DataNegotiationMono)
                .map(tuple -> {
                    var mvBrokerEntities4DataNegotiation = tuple.getT1();
                    var mvAuditServiceEntities4DataNegotiation = tuple.getT2();

                    return mvBrokerEntities4DataNegotiation.stream()
                            .map(brokerEntity -> {
                                var auditServiceEntity = mvAuditServiceEntities4DataNegotiation.stream()
                                        .filter(auditEntity -> auditEntity.id().equals(brokerEntity.id()))
                                        .findFirst()
                                        .orElse(null);

                                if (auditServiceEntity != null) {
                                    return new MVEntity4DataNegotiation(
                                            brokerEntity.id(),
                                            brokerEntity.type(),
                                            brokerEntity.version(),
                                            brokerEntity.lastUpdate(),
                                            auditServiceEntity.hash(),
                                            auditServiceEntity.hashlink());
                                } else {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .toList();
                });
    }
}
