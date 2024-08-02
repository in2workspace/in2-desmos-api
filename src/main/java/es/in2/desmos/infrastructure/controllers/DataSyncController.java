package es.in2.desmos.infrastructure.controllers;

import es.in2.desmos.application.workflows.jobs.P2PDataSyncJob;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync/p2p")
@RequiredArgsConstructor
public class DataSyncController {

    private final DataSyncService dataSyncService;
    private final P2PDataSyncJob p2PDataSyncJob;
    private final BrokerConfig brokerConfig;
    private final ApiConfig apiConfig;

    @GetMapping("/data")
    public Mono<Void> synchronizeData() {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting Data Synchronization...", processId);

        // TODO decide if we wanna go through the p2p or dataSync
        return dataSyncService.synchronizeData(processId);
    }

    @PostMapping("/discovery")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DiscoverySyncResponse> discoverySync(@RequestBody @Valid Mono<DiscoverySyncRequest> discoverySyncRequest) {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting P2P Data Synchronization Discovery Controller", processId);

        return discoverySyncRequest.flatMap(request -> {
                    log.debug("ProcessID: {} - Starting P2P Data Synchronization Discovery: {}", processId, request);
                    Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiationMono = Mono.just(request.externalMVEntities4DataNegotiation());
                    Mono<String> issuerMono = Mono.just(request.issuer());
                    return p2PDataSyncJob.dataDiscovery(processId, issuerMono, externalMvEntities4DataNegotiationMono)
                            .flatMap(localMvEntities4DataNegotiation -> {
                                Mono<List<MVEntity4DataNegotiation>> localMvEntities4DataNegotiationMono = Mono.just(localMvEntities4DataNegotiation);

                                return localMvEntities4DataNegotiationMono.map(mvEntities4DataNegotiation ->
                                        new DiscoverySyncResponse(apiConfig.getExternalDomain(), mvEntities4DataNegotiation));
                            });
                })
                .doOnSuccess(success -> log.info("ProcessID: {} - P2P Data Synchronization Discovery successfully.", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the P2P Data Synchronization Discovery Controller: {}", processId, error.getMessage()));
    }

    @PostMapping(value = "/entities")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<String>> entitiesSync(@RequestBody @Valid Mono<@NotNull Id[]> entitySyncRequest) {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting P2P Entities Synchronization Controller", processId);

        return entitySyncRequest.flatMapMany(Flux::fromArray)
                .collectList()
                .flatMap(ids -> {
                    log.debug("ProcessID: {} - Starting P2P Entities Synchronization: {}", processId, ids);

                    Mono<List<Id>> idsMono = Mono.just(ids);
                    return p2PDataSyncJob.getLocalEntitiesByIdInBase64(processId, idsMono);
                })
                .doOnSuccess(success -> log.info("ProcessID: {} - P2P Entities Synchronization successfully.", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the P2P Entities Synchronization Controller: {}", processId, error.getMessage()));
    }

}
