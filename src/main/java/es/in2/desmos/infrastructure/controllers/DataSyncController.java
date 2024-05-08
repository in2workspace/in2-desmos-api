package es.in2.desmos.infrastructure.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.sync.jobs.P2PDataSyncJob;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import jakarta.validation.Valid;
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
    private final ObjectMapper objectMapper;

    @GetMapping("/data")
    public Mono<Void> synchronizeData() {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting Data Synchronization...", processId);
        return dataSyncService.synchronizeData(processId); // todo: decide if we wanna go through the p2p or dataSync
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
                                        new DiscoverySyncResponse(brokerConfig.getExternalDomain(), mvEntities4DataNegotiation));
                            });
                })
                .doOnSuccess(success -> log.info("ProcessID: {} - P2P Data Synchronization Discovery successfully.", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the P2P Data Synchronization Discovery Controller: {}", processId, error.getMessage()));
    }

    @PostMapping(value = "/entities")
    @ResponseStatus(HttpStatus.OK)
    public Mono<JsonNode> entitiesSync(@RequestBody @Valid Mono<Id[]> entitySyncRequest) {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting P2P Entities Synchronization Controller", processId);

        return entitySyncRequest.flatMapMany(Flux::fromArray)
                .collectList()
                .flatMap(ids -> {
                    log.debug("ProcessID: {} - Starting P2P Entities Synchronization: {}", processId, ids);
                    Mono<List<Id>> idsMono = Mono.just(ids);
                    return p2PDataSyncJob.getLocalEntitiesById(processId, idsMono)
                            .flatMapIterable(entities -> entities)
                            .map(entity -> {
                                JsonArray jsonArray = new JsonArray();
                                jsonArray.add(entity);
                                return jsonArray;
                            })
                            .collectList()
                            .flatMap(jsonObjects -> {
                                JsonArray jsonArray = new JsonArray();
                                for (JsonArray array : jsonObjects) {
                                    jsonArray.addAll(array);
                                }

                                try {
                                    return Mono.just(objectMapper.readTree(jsonArray.toString()));
                                } catch (JsonProcessingException e) {
                                    return Mono.error(e);
                                }
                            });
                })
                .doOnSuccess(success -> log.info("ProcessID: {} - P2P Entities Synchronization successfully.", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the P2P Entities Synchronization Controller: {}", processId, error.getMessage()));
    }

}
