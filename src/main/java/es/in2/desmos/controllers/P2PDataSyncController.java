package es.in2.desmos.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.workflows.P2PDataSyncWorkflow;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class P2PDataSyncController {
    private final P2PDataSyncWorkflow p2PDataSyncWorkflow;

    private final ObjectMapper objectMapper;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    @PostMapping("/discovery")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DiscoverySyncResponse> discoverySync(@RequestBody @Valid Mono<DiscoverySyncRequest> discoverySyncRequest) {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting P2P Data Synchronization Discovery Controller", processId);

        return discoverySyncRequest.flatMap(request -> {
                    log.debug("ProcessID: {} - Starting P2P Data Synchronization Discovery: {}", processId, request);

                    Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiationMono = Mono.just(request.externalMVEntities4DataNegotiation());
                    Mono<String> issuerMono = Mono.just(request.issuer());

                    return p2PDataSyncWorkflow.dataDiscovery(processId, issuerMono, externalMvEntities4DataNegotiationMono)
                            .flatMap(localMvEntities4DataNegotiation -> {
                                Mono<List<MVEntity4DataNegotiation>> localMvEntities4DataNegotiationMono = Mono.just(localMvEntities4DataNegotiation);

                                return localMvEntities4DataNegotiationMono.map(mvEntities4DataNegotiation ->
                                        new DiscoverySyncResponse(contextBrokerExternalDomain, mvEntities4DataNegotiation));
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

                    return p2PDataSyncWorkflow.getLocalEntitiesById(ids)
                            .flatMapIterable(entities -> entities)
                            .map(entity -> JsonParser.parseString(entity).getAsJsonObject())
                            .collectList()
                            .flatMap(jsonObjects -> {
                                try {
                                    return Mono.just(objectMapper.readTree(jsonObjects.toString()));
                                } catch (JsonProcessingException e) {
                                    return Mono.error(e);
                                }
                            });
                })
                .doOnSuccess(success -> log.info("ProcessID: {} - P2P Entities Synchronization successfully.", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the P2P Entities Synchronization Controller: {}", processId, error.getMessage()));
    }
}
