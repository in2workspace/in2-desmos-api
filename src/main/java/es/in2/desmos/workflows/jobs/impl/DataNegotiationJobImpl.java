package es.in2.desmos.workflows.jobs.impl;

import es.in2.desmos.domain.models.DataNegotiationEvent;
import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.workflows.jobs.DataNegotiationJob;
import es.in2.desmos.workflows.jobs.DataTransferJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataNegotiationJobImpl implements DataNegotiationJob {
    private final DataTransferJob dataTransferJob;

    @Override
    public Mono<Void> negotiateDataSync(DataNegotiationEvent dataNegotiationEvent) {
        log.info("Listening data negotiation event.");
        Mono<List<MVEntity4DataNegotiation>> newEntitiesToSync =
                checkWithExternalDataIsMissing(
                        dataNegotiationEvent.externalEntityIds(),
                        dataNegotiationEvent.localEntityIds());

        Mono<List<MVEntity4DataNegotiation>> existingEntitiesToSync =
                checkVersionsAndLastUpdateFromEntityIdMatched(
                        dataNegotiationEvent.externalEntityIds(),
                        dataNegotiationEvent.localEntityIds());

        Mono<DataNegotiationResult> dataNegotiationResult = createDataNegotiationResult(dataNegotiationEvent, newEntitiesToSync, existingEntitiesToSync);

        return dataTransferJob.syncData(dataNegotiationResult);
    }

    private Mono<List<MVEntity4DataNegotiation>> checkWithExternalDataIsMissing(
            Mono<List<MVEntity4DataNegotiation>> externalEntityIds,
            Mono<List<MVEntity4DataNegotiation>> localEntityIds) {
        return Mono.zip(externalEntityIds, localEntityIds)
                .map(tuple -> {
                    List<MVEntity4DataNegotiation> externalList = tuple.getT1();
                    List<MVEntity4DataNegotiation> localList = tuple.getT2();

                    return getNotExistentItems(externalList, localList);
                });
    }

    private List<MVEntity4DataNegotiation> getNotExistentItems(List<MVEntity4DataNegotiation> originalList, List<MVEntity4DataNegotiation> itemsToCheck) {
        return originalList.stream()
                .filter(externalEntity -> itemsToCheck.stream()
                        .noneMatch(entity -> entity.id().equals(externalEntity.id())))
                .toList();
    }

    private Mono<List<MVEntity4DataNegotiation>> checkVersionsAndLastUpdateFromEntityIdMatched(
            Mono<List<MVEntity4DataNegotiation>> externalEntityIds,
            Mono<List<MVEntity4DataNegotiation>> localEntityIds) {
        return Mono.zip(externalEntityIds, localEntityIds)
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
                                                    isExternalEntityVersionNewer(externalEntity.getFloatVersion(), sameLocalEntity.getFloatVersion()) ||
                                                            (isVersionEqual(externalEntity.getFloatVersion(), sameLocalEntity.getFloatVersion()) &&
                                                                    isExternalEntityLastUpdateNewer(externalEntity.getInstantLastUpdate(), sameLocalEntity.getInstantLastUpdate()))
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