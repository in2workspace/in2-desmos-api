package es.in2.desmos.workflows.jobs.impl;

import es.in2.desmos.domain.models.DataNegotiationEvent;
import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.Issuer;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.workflows.jobs.DataNegotiationJob;
import es.in2.desmos.workflows.jobs.DataTransferJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataNegotiationJobImpl implements DataNegotiationJob {
    private final DataTransferJob dataTransferJob;

    @Override
    public Mono<Void> negotiateDataSync(String processId, Mono<Map<Issuer, List<MVEntity4DataNegotiation>>> localMvEntities4DataNegotiationMono, Mono<List<MVEntity4DataNegotiation>> mvEntities4DataNegotiationMono) {
        // TODO
        return null;
    }

    @Override
    public Mono<Void> negotiateDataSync(DataNegotiationEvent dataNegotiationEvent) {
        String processId = dataNegotiationEvent.processId();

        log.info("ProcessID: {} - Starting Data Negotiation Job", processId);

        Mono<List<MVEntity4DataNegotiation>> externalMVEntities4DataNegotiationMono = dataNegotiationEvent.externalMVEntities4DataNegotiation();
        Mono<List<MVEntity4DataNegotiation>> localMVEntities4DataNegotiationMono = dataNegotiationEvent.localMVEntities4DataNegotiation();

        return checkWithExternalDataIsMissing(externalMVEntities4DataNegotiationMono, localMVEntities4DataNegotiationMono)
                .zipWith(checkVersionsAndLastUpdateFromEntityIdMatched(externalMVEntities4DataNegotiationMono, localMVEntities4DataNegotiationMono))
                .flatMap(tuple -> {
                    List<MVEntity4DataNegotiation> externalMVEntities4DataNegotiation = tuple.getT1();
                    List<MVEntity4DataNegotiation> localMVEntities4DataNegotiation = tuple.getT2();

                    log.debug("ProcessID: {} - External MV Entities 4 Data Negotiation: {}", processId, externalMVEntities4DataNegotiation);
                    log.debug("ProcessID: {} - Local MV Entities 4 Data Negotiation: {}", processId, localMVEntities4DataNegotiation);

                    return createDataNegotiationResult(dataNegotiationEvent, Mono.just(externalMVEntities4DataNegotiation), Mono.just(localMVEntities4DataNegotiation));
                })
                .flatMap(dataNegotiationResult -> {
                    log.debug("ProcessID: {} - Data Negotiation Result: {}", dataNegotiationEvent.processId(), dataNegotiationResult);
                    return dataTransferJob.syncData(dataNegotiationEvent.processId(), Mono.just(dataNegotiationResult));
                });
    }

    private Mono<List<MVEntity4DataNegotiation>> checkWithExternalDataIsMissing(
            Mono<List<MVEntity4DataNegotiation>> externalEntityIds,
            Mono<List<MVEntity4DataNegotiation>> localEntityIds) {
        return externalEntityIds.zipWith(localEntityIds)
                .map(tuple -> {
                    List<MVEntity4DataNegotiation> originalList = tuple.getT1();
                    Set<String> idsToCheck = tuple.getT2().stream()
                            .map(MVEntity4DataNegotiation::id)
                            .collect(Collectors.toSet());

                    return originalList.stream()
                            .filter(entity -> !idsToCheck.contains(entity.id()))
                            .toList();
                });
    }

    private Mono<List<MVEntity4DataNegotiation>> checkVersionsAndLastUpdateFromEntityIdMatched(
            Mono<List<MVEntity4DataNegotiation>> externalEntityIds,
            Mono<List<MVEntity4DataNegotiation>> localEntityIds) {
        return externalEntityIds.zipWith(localEntityIds)
                .map(tuple -> {
                    List<MVEntity4DataNegotiation> externalList = tuple.getT1();
                    List<MVEntity4DataNegotiation> localList = tuple.getT2();

                    return externalList
                            .stream()
                            .filter(externalEntity ->
                                    localList
                                            .stream()
                                            .filter(localEntity -> localEntity.id().equals(externalEntity.id()))
                                            .findFirst()
                                            .map(sameLocalEntity ->
                                                    {
                                                        Float externalEntityVersion = externalEntity.getFloatVersion();
                                                        Float sameLocalEntityVersion = sameLocalEntity.getFloatVersion();
                                                        return isExternalEntityVersionNewer(
                                                                externalEntityVersion,
                                                                sameLocalEntityVersion) ||
                                                                (isVersionEqual(
                                                                        externalEntityVersion,
                                                                        sameLocalEntityVersion) &&
                                                                        isExternalEntityLastUpdateNewer(
                                                                                externalEntity.getInstantLastUpdate(),
                                                                                sameLocalEntity.getInstantLastUpdate()));
                                                    }
                                            )
                                            .orElse(false))
                            .toList();
                });
    }

    private boolean isExternalEntityVersionNewer(Float externalEntityVersion, Float sameLocalEntityVersion) {
        return externalEntityVersion > sameLocalEntityVersion;
    }

    private boolean isVersionEqual(Float externalEntityVersion, Float sameLocalEntityVersion) {
        return Objects.equals(externalEntityVersion, sameLocalEntityVersion);
    }

    private boolean isExternalEntityLastUpdateNewer(Instant externalEntityLastUpdate, Instant sameLocalEntityLastUpdate) {
        return externalEntityLastUpdate.isAfter(sameLocalEntityLastUpdate);
    }

    private Mono<DataNegotiationResult> createDataNegotiationResult(DataNegotiationEvent dataNegotiationEvent, Mono<List<MVEntity4DataNegotiation>> newEntitiesToSync, Mono<List<MVEntity4DataNegotiation>> existingEntitiesToSync) {
        return Mono.zip(dataNegotiationEvent.issuer(), newEntitiesToSync, existingEntitiesToSync).map(
                tuple -> {
                    String issuer = tuple.getT1();
                    List<MVEntity4DataNegotiation> newEntitiesToSyncValue = tuple.getT2();
                    List<MVEntity4DataNegotiation> existingEntitiesToSyncValue = tuple.getT3();

                    return new DataNegotiationResult(issuer, newEntitiesToSyncValue, existingEntitiesToSyncValue);
                }
        );
    }
}