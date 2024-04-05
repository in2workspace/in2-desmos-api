package es.in2.desmos.controllers;

import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.workflows.DiscoverySyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync/discovery")
@RequiredArgsConstructor
public class DiscoverySyncController {
    private final DiscoverySyncWorkflow discoverySyncWorkflow;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<DiscoverySyncResponse> discoverySync(@RequestBody DiscoverySyncRequest discoverySyncRequest) {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting Synchronization Discovery...", processId);

        List<ProductOffering> localEntitiesIds = discoverySyncWorkflow.discoverySync(processId, discoverySyncRequest.issuer(), discoverySyncRequest.externalEntityIds());


        return Mono.just(new DiscoverySyncResponse(discoverySyncRequest.issuer(), localEntitiesIds));
    }
}
