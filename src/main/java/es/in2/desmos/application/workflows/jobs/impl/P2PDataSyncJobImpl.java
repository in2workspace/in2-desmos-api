package es.in2.desmos.application.workflows.jobs.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.application.workflows.jobs.DataNegotiationJob;
import es.in2.desmos.application.workflows.jobs.P2PDataSyncJob;
import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.DiscoverySyncWebClient;
import es.in2.desmos.domain.utils.ApplicationUtils;
import es.in2.desmos.domain.utils.Base64Converter;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.ExternalAccessNodesConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class P2PDataSyncJobImpl implements P2PDataSyncJob {
    private final ExternalAccessNodesConfig externalAccessNodesConfig;

    private final ApiConfig apiConfig;

    private final BrokerPublisherService brokerPublisherService;

    private final AuditRecordService auditRecordService;

    private final DataNegotiationEventPublisher dataNegotiationEventPublisher;

    private final DataNegotiationJob dataNegotiationJob;

    private final DiscoverySyncWebClient discoverySyncWebClient;

    private static final String[] BROKER_ENTITY_TYPES = {"product-offering", "category", "catalog"};

    @Override
    public Mono<Void> synchronizeData(String processId) {
        log.info("ProcessID: {} - Starting P2P Data Synchronization Workflow", processId);

        return Flux.fromIterable(Arrays.asList(BROKER_ENTITY_TYPES))
                .concatMap(entityType ->
                        createLocalMvEntities4DataNegotiationByEntityType(processId, entityType)
                                .flatMap(localMvEntities4DataNegotiation -> {
                                    log.debug("ProcessID: {} - Local MV Entities 4 Data Negotiation synchronizing data: {}", processId, localMvEntities4DataNegotiation);
                                    log.info("HOLAAA ProcessID: {} - Local MV Entities 4 Data Negotiation synchronizing data: {}", processId, localMvEntities4DataNegotiation);
                                    return getExternalMVEntities4DataNegotiationByIssuer(processId, localMvEntities4DataNegotiation, entityType)
                                            .flatMap(mvEntities4DataNegotiationByIssuer -> {
                                                Mono<Map<Issuer, List<MVEntity4DataNegotiation>>> externalMVEntities4DataNegotiationByIssuerMono = Mono.just(mvEntities4DataNegotiationByIssuer);
                                                Mono<List<MVEntity4DataNegotiation>> localMVEntities4DataNegotiationMono = Mono.just(localMvEntities4DataNegotiation);

                                                return dataNegotiationJob.negotiateDataSyncWithMultipleIssuers(processId, externalMVEntities4DataNegotiationByIssuerMono, localMVEntities4DataNegotiationMono);
                                            });
                                }))
                .collectList()
                .then();
    }

    private Mono<Map<Issuer, List<MVEntity4DataNegotiation>>> getExternalMVEntities4DataNegotiationByIssuer(String processId, List<MVEntity4DataNegotiation> localMvEntities4DataNegotiation, String entityType) {
        return externalAccessNodesConfig.getExternalAccessNodesUrls()
                .flatMapIterable(externalAccessNodesList -> externalAccessNodesList)
                .flatMap(externalAccessNode -> {
                    log.debug("ProcessID: {} - External Access Node: {}", processId, externalAccessNode);
                    var discoverySyncRequest = new DiscoverySyncRequest(apiConfig.getExternalDomain(), localMvEntities4DataNegotiation);

                    Mono<DiscoverySyncRequest> discoverySyncRequestMono = Mono.just(discoverySyncRequest);

                    return discoverySyncWebClient.makeRequest(processId, Mono.just(externalAccessNode), discoverySyncRequestMono)
                            .map(resultList -> {
                                log.debug("ProcessID: {} - Get DiscoverySync Response. [issuer={}, response={}]", processId, externalAccessNode, resultList);

                                Issuer issuer = new Issuer(externalAccessNode);

                                var filteredEntitiesByType =
                                        resultList
                                                .entities()
                                                .stream()
                                                .filter(mvEntity4DataNegotiation -> Objects.equals(mvEntity4DataNegotiation.type(), entityType))
                                                .toList();

                                log.info("HOLAAA ProcessID: {} - DiscoverySync Response filtered. [issuer={}, response={}]", entityType, externalAccessNode, filteredEntitiesByType);
                                log.debug("ProcessID: {} - DiscoverySync Response filtered. [issuer={}, response={}]", processId, externalAccessNode, filteredEntitiesByType);

                                return Map.entry(issuer, filteredEntitiesByType);
                            });
                })
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    @Override
    public Mono<List<MVEntity4DataNegotiation>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiationMono) {
        log.info("ProcessID: {} - Starting P2P Data Synchronization Discovery Workflow", processId);

        return Flux.fromIterable(Arrays.asList(BROKER_ENTITY_TYPES))
                .concatMap(entityType ->
                        createLocalMvEntities4DataNegotiationByEntityType(processId, entityType)
                                .flatMap(localMvEntities4DataNegotiation -> {
                                    log.debug("ProcessID: {} - Local MV Entities 4 Data Negotiation: {}", processId, localMvEntities4DataNegotiation);
                                    log.info("HOLAAA ProcessID: {} - Local MV Entities 4 Data Negotiation: {}", entityType, localMvEntities4DataNegotiation);

                                    return externalMvEntities4DataNegotiationMono
                                            .flatMap(externalMvEntities4DataNegotiation -> {
                                                List<MVEntity4DataNegotiation> externalMvEntities4DataNegotiationOfType = externalMvEntities4DataNegotiation
                                                        .stream()
                                                        .filter(mvEntity4DataNegotiation -> Objects.equals(mvEntity4DataNegotiation.type(), entityType))
                                                        .toList();

                                                var localMvEntities4DataNegotiationMono = Mono.just(localMvEntities4DataNegotiation);

                                                var dataNegotiationEvent = new DataNegotiationEvent(processId, issuer, Mono.just(externalMvEntities4DataNegotiationOfType), localMvEntities4DataNegotiationMono);
                                                dataNegotiationEventPublisher.publishEvent(dataNegotiationEvent);

                                                return Mono.just(localMvEntities4DataNegotiation);
                                            });
                                })
                                .doOnSuccess(success -> log.info("ProcessID: {} - P2P Data Synchronization Discovery Workflow successfully.", processId))
                                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the P2P Data Synchronization Discovery Workflow: {}", processId, error.getMessage())))
                .flatMap(Flux::fromIterable)
                .collectList();
    }

    @Override
    public Mono<List<String>> getLocalEntitiesByIdInBase64(String processId, Mono<List<Id>> ids) {
        return brokerPublisherService
                .findAllById(processId, ids, new ArrayList<>())
                .doOnSuccess(allEntitiesAndSubEntities ->
                        log.debug("ProcessID: {} - Found all local entities with sub-entities in Scorpio. [entities={}]", processId, allEntitiesAndSubEntities))
                .map(Base64Converter::convertStringListToBase64List)
                .doOnSuccess(base64Entities ->
                        log.debug("ProcessID: {} - Convert all local entities with sub-entities in Scorpio to Base64. [entities={}]", processId, base64Entities));
    }

    private static Mono<List<String>> getEntities4DataNegotiationIds(Mono<List<BrokerEntityWithIdTypeLastUpdateAndVersion>> mvBrokerEntities4DataNegotiationMono) {
        return mvBrokerEntities4DataNegotiationMono.map(x -> x.stream().map(BrokerEntityWithIdTypeLastUpdateAndVersion::getId).toList());
    }

    private Mono<List<MVEntity4DataNegotiation>> createLocalMvEntities4DataNegotiationByEntityType(String processId, String brokerEntityType) {
        return brokerPublisherService.findAllIdTypeAndAttributesByType(processId, brokerEntityType, "lastUpdate", "version", "lifecycleStatus", "validFor", BrokerEntityWithIdTypeLastUpdateAndVersion[].class)
                .flatMap(mvBrokerEntities4DataNegotiation -> {
                    log.debug("ProcessID: {} - MV Broker Entities 4 Data Negotiation: {}", processId, mvBrokerEntities4DataNegotiation);

                    Mono<List<String>> entities4DataNegotiationIdsMono = getEntities4DataNegotiationIds(Mono.just(mvBrokerEntities4DataNegotiation));

                    return getMvAuditServiceEntities4DataNegotiation(processId, entities4DataNegotiationIdsMono)
                            .flatMap(mvAuditServiceEntities4DataNegotiation -> {
                                log.debug("ProcessID: {} - MV Audit Service Entities 4 Data Negotiation: {}", processId, mvAuditServiceEntities4DataNegotiation);

                                Map<String, MVAuditServiceEntity4DataNegotiation> auditServiceEntityMap = mvAuditServiceEntities4DataNegotiation.stream()
                                        .collect(Collectors.toMap(MVAuditServiceEntity4DataNegotiation::id, Function.identity()));

                                return Flux.fromIterable(mvBrokerEntities4DataNegotiation)
                                        .flatMap(brokerEntity -> {
                                            MVAuditServiceEntity4DataNegotiation auditServiceEntity = auditServiceEntityMap.get(brokerEntity.getId());

                                            if (auditServiceEntity != null) {
                                                return getEntityHash(processId, Mono.just(brokerEntity.getId()))
                                                        .map(hash -> new MVEntity4DataNegotiation(
                                                                brokerEntity.getId(),
                                                                brokerEntityType,
                                                                brokerEntity.getVersion(),
                                                                brokerEntity.getLastUpdate(),
                                                                brokerEntity.getLifecycleStatus(),
                                                                brokerEntity.getValidFor().startDateTime(),
                                                                hash,
                                                                auditServiceEntity.hashlink()
                                                        ));
                                            } else {
                                                return Mono.empty();
                                            }
                                        })
                                        .collectList();
                            });
                });
    }


    private Mono<String> getEntityHash(String processId, Mono<String> entityIdMono) {
        return entityIdMono.flatMap(entityId ->
                brokerPublisherService.getEntityById(processId, entityId)
                        .flatMap(entity -> {
                            try {
                                String hash = ApplicationUtils.calculateSHA256(entity);
                                return Mono.just(hash);
                            } catch (NoSuchAlgorithmException | JsonProcessingException e) {
                                return Mono.error(e);
                            }
                        }));
    }

    private Mono<List<MVAuditServiceEntity4DataNegotiation>> getMvAuditServiceEntities4DataNegotiation(String processId, Mono<List<String>> entities4DataNegotiationIdsMono) {
        return entities4DataNegotiationIdsMono.flatMap(entities4DataNegotiationIds ->
                Flux.fromIterable(entities4DataNegotiationIds)
                        .flatMap(id ->
                                auditRecordService.findLatestAuditRecordForEntity(processId, id)
                                        .map(auditRecord ->
                                                new MVAuditServiceEntity4DataNegotiation(
                                                        auditRecord.getEntityId(),
                                                        auditRecord.getEntityType(),
                                                        auditRecord.getEntityHash(),
                                                        auditRecord.getEntityHashLink()
                                                )
                                        )
                        )
                        .collectList()
        );
    }

}
