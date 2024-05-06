package es.in2.desmos.domain.services.sync.jobs.impl;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.DiscoverySyncWebClient;
import es.in2.desmos.domain.services.sync.jobs.DataNegotiationJob;
import es.in2.desmos.domain.services.sync.jobs.P2PDataSyncJob;
import es.in2.desmos.infrastructure.configs.ExternalAccessNodesConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class P2PDataSyncJobImpl implements P2PDataSyncJob {
    private final ExternalAccessNodesConfig externalAccessNodesConfig;

    private final BrokerPublisherService brokerPublisherService;

    private final AuditRecordService auditRecordService;

    private final DataNegotiationEventPublisher dataNegotiationEventPublisher;

    private final DataNegotiationJob dataNegotiationJob;

    private final DiscoverySyncWebClient discoverySyncWebClient;

    private static final String BROKER_TYPE = "ProductOffering";

    @Override
    public Mono<Void> synchronizeData(String processId) {
        log.info("ProcessID: {} - Starting P2P Data Synchronization Workflow", processId);

        return createLocalMvEntities4DataNegotiation(processId).flatMap(localMvEntities4DataNegotiation -> {
            log.debug("ProcessID: {} - Local MV Entities 4 Data Negotiation synchronizing data: {}", processId, localMvEntities4DataNegotiation);

            return getExternalMVEntities4DataNegotiationByIssuer(processId, localMvEntities4DataNegotiation)
                    .flatMap(mvEntities4DataNegotiationByIssuer -> {
                        Mono<Map<Issuer, List<MVEntity4DataNegotiation>>> externalMVEntities4DataNegotiationByIssuerMono = Mono.just(mvEntities4DataNegotiationByIssuer);
                        Mono<List<MVEntity4DataNegotiation>> localMVEntities4DataNegotiationMono = Mono.just(localMvEntities4DataNegotiation);

                        return dataNegotiationJob.negotiateDataSyncWithMultipleIssuers(processId, externalMVEntities4DataNegotiationByIssuerMono, localMVEntities4DataNegotiationMono);
                    });
        });
    }

    private Mono<Map<Issuer, List<MVEntity4DataNegotiation>>> getExternalMVEntities4DataNegotiationByIssuer(String processId, List<MVEntity4DataNegotiation> localMvEntities4DataNegotiation) {
        return externalAccessNodesConfig.getExternalAccessNodesUrls()
                .flatMapIterable(externalAccessNodesList -> externalAccessNodesList)
                .flatMap(externalAccessNode -> {
                    Issuer issuer = new Issuer(externalAccessNode);
                    Mono<MVEntity4DataNegotiation[]> localMvEntities4DataNegotiationMono = Mono.just(localMvEntities4DataNegotiation.toArray(MVEntity4DataNegotiation[]::new));

                    return discoverySyncWebClient.makeRequest(processId, Mono.just(externalAccessNode), localMvEntities4DataNegotiationMono)
                            .map(resultList -> Map.entry(issuer, Arrays.stream(resultList).toList()));
                })
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

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

    @Override
    public Mono<List<String>> getLocalEntitiesById(String processId, Mono<List<Id>> ids) {
        return brokerPublisherService.findAllById(processId, ids);
    }

    private Mono<List<MVEntity4DataNegotiation>> createLocalMvEntities4DataNegotiation(String processId) {
        return brokerPublisherService.getMVBrokerEntities4DataNegotiation(processId, BROKER_TYPE, "lastUpdate", "version")
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
