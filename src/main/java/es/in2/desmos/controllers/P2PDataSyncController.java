package es.in2.desmos.controllers;

import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.workflows.P2PDataSyncWorkflow;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync/p2p")
@RequiredArgsConstructor
public class P2PDataSyncController {
    private final P2PDataSyncWorkflow p2PDataSyncWorkflow;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    @PostMapping("/discovery")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DiscoverySyncResponse> discoverySync(@RequestBody @Valid Mono<DiscoverySyncRequest> discoverySyncRequest) {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting Synchronization Discovery", processId);

        return discoverySyncRequest.flatMap(request -> {
                    log.debug("ProcessID: {} - Starting Synchronization Discovery: {}", processId, request);

                    Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiationMono = Mono.just(request.mvEntities4DataNegotiation());
                    Mono<String> issuerMono = Mono.just(request.issuer());

                    return p2PDataSyncWorkflow.dataDiscovery(processId, issuerMono, externalMvEntities4DataNegotiationMono)
                            .flatMap(localMvEntities4DataNegotiation -> {
                                Mono<List<MVEntity4DataNegotiation>> localMvEntities4DataNegotiationMono = Mono.just(localMvEntities4DataNegotiation);

                                return localMvEntities4DataNegotiationMono.map(mvEntities4DataNegotiation ->
                                        new DiscoverySyncResponse(contextBrokerExternalDomain, mvEntities4DataNegotiation));
                            });
                })
                .doOnSuccess(success -> log.info("ProcessID: {} - Subscribe Workflow completed successfully.", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error occurred while processing the Subscribe Workflow: {}", processId, error.getMessage()));
    }
}
