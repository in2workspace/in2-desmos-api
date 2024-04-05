package es.in2.desmos.controllers;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.models.SyncDiscoveryRequest;
import es.in2.desmos.domain.models.SyncDiscoveryResponse;
import es.in2.desmos.domain.services.sync.SyncDiscoveryService;
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
public class SyncDiscoveryController {
    private final SyncDiscoveryService syncDiscoveryService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<SyncDiscoveryResponse> syncDiscovery(@RequestBody SyncDiscoveryRequest syncDiscoveryRequest) {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting Synchronization Discovery...", processId);

        List<ProductOffering> localEntitiesIds = syncDiscoveryService.syncDiscovery(processId, syncDiscoveryRequest.issuer(), syncDiscoveryRequest.externalEntityIds());


        return Mono.just(new SyncDiscoveryResponse(syncDiscoveryRequest.issuer(), localEntitiesIds));
    }
}
