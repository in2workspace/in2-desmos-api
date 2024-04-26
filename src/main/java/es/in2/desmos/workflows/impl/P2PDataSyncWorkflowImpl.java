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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class P2PDataSyncWorkflowImpl implements P2PDataSyncWorkflow {
    private final BrokerEntityGetterService brokerEntityGetterService;

    private final AuditRecordService auditRecordService;

    private final DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Override
    public Mono<List<MVEntity4DataNegotiation>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiationMono) {
        log.info("ProcessID: {} - Starting P2P Data Synchronization Discovery Workflow", processId);

        return createLocalMvEntities4DataNegotiation(processId).map(localMvEntities4DataNegotiation -> {
                    log.debug("ProcessID: {} - Local MV Entities 4 Data Negotiation: {}", processId, localMvEntities4DataNegotiation);

                    var localMvEntities4DataNegotiationMono = Mono.just(localMvEntities4DataNegotiation);

                    var dataNegotiationEvent = new DataNegotiationEvent(processId, issuer, externalMvEntities4DataNegotiationMono, localMvEntities4DataNegotiationMono);
                    dataNegotiationEventPublisher.publishEvent(dataNegotiationEvent);

                    return localMvEntities4DataNegotiation;
                })
                .doOnSuccess(success -> log.info("ProcessID: {} - P2P Data Synchronization Discovery Workflow successfully.", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the P2P Data Synchronization Discovery Workflow: {}", processId, error.getMessage()));
    }

    private Mono<List<MVEntity4DataNegotiation>> createLocalMvEntities4DataNegotiation(String processId) {
        return brokerEntityGetterService.getMVBrokerEntities4DataNegotiation(processId)
                .flatMap(mvBrokerEntities4DataNegotiation -> {
                    log.debug("ProcessID: {} - MV Broker Entities 4 Data Negotiation: {}", processId, mvBrokerEntities4DataNegotiation);

                    Mono<List<String>> entities4DataNegotiationIdsMono = getEntities4DataNegotiationIds(Mono.just(mvBrokerEntities4DataNegotiation));

                    return getMvAuditServiceEntities4DataNegotiation(processId, entities4DataNegotiationIdsMono)
                            .map(mvAuditServiceEntities4DataNegotiation -> {
                                log.debug("ProcessID: {} - MV Audit Service Entities 4 Data Negotiation: {}", processId, mvAuditServiceEntities4DataNegotiation);

                                Map<String, MVAuditServiceEntity4DataNegotiation> auditServiceEntityMap = mvAuditServiceEntities4DataNegotiation.stream()
                                        .collect(Collectors.toMap(MVAuditServiceEntity4DataNegotiation::id, Function.identity()));

                                return mvBrokerEntities4DataNegotiation.stream()
                                        .map(brokerEntity -> {
                                            MVAuditServiceEntity4DataNegotiation auditServiceEntity = auditServiceEntityMap.get(brokerEntity.id());
                                            return new MVEntity4DataNegotiation(
                                                    brokerEntity.id(),
                                                    brokerEntity.type(),
                                                    brokerEntity.version(),
                                                    brokerEntity.lastUpdate(),
                                                    auditServiceEntity.hash(),
                                                    auditServiceEntity.hashlink());
                                        })
                                        .toList();
                            });
                });
    }

    private static Mono<List<String>> getEntities4DataNegotiationIds(Mono<List<MVBrokerEntity4DataNegotiation>> mvBrokerEntities4DataNegotiationMono) {
        return mvBrokerEntities4DataNegotiationMono.map(x -> x.stream().map(MVBrokerEntity4DataNegotiation::id).toList());
    }

    private Mono<List<MVAuditServiceEntity4DataNegotiation>> getMvAuditServiceEntities4DataNegotiation(String processId, Mono<List<String>> entities4DataNegotiationIdsMono) {
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
}
