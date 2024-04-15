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
                checkVersionsAndTimestampsFromEntityIdMatched(
                        dataNegotiationEvent.externalEntityIds(),
                        dataNegotiationEvent.localEntityIds());

        Mono<DataNegotiationResult> dataNegotiationResult =
                createDataNegotiationResult(
                        dataNegotiationEvent.issuer(),
                        newEntitiesToSync,
                        existingEntitiesToSync);

        return dataTransferJob.syncData(dataNegotiationResult);
    }

    private Mono<List<MVEntity4DataNegotiation>> checkWithExternalDataIsMissing(
            Mono<List<MVEntity4DataNegotiation>> externalEntityIds,
            Mono<List<MVEntity4DataNegotiation>> localEntityIds) {
        return Mono.zip(externalEntityIds, localEntityIds)
                .map(tuple -> {
                    List<MVEntity4DataNegotiation> externalList = tuple.getT1();
                    List<MVEntity4DataNegotiation> localList = tuple.getT2();

                    return externalList.stream()
                            .filter(externalEntity -> localList.stream()
                                    .noneMatch(entity -> entity.id().equals(externalEntity.id())))
                            .toList();
                });
    }

    private Mono<List<MVEntity4DataNegotiation>> checkVersionsAndTimestampsFromEntityIdMatched(
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
                                                    Float.parseFloat(externalEntity.version().substring(1)) >
                                                            Float.parseFloat(sameLocalEntity.version().substring(1)) ||
                                                            (Float.parseFloat(externalEntity.version().substring(1)) ==
                                                                    Float.parseFloat(sameLocalEntity.version().substring(1)) &&
                                                                    Instant.parse(externalEntity.lastUpdate()).isAfter(Instant.parse(sameLocalEntity.lastUpdate())))
                                            )
                                            .orElse(false))
                            .toList();
                });
    }

    private Mono<DataNegotiationResult> createDataNegotiationResult(Mono<String> issuer, Mono<List<MVEntity4DataNegotiation>> newEntitiesToSync, Mono<List<MVEntity4DataNegotiation>> existingEntitiesToSync) {
        return Mono.just(new DataNegotiationResult(issuer, newEntitiesToSync, existingEntitiesToSync));
    }
}