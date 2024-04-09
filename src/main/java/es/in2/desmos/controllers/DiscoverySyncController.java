package es.in2.desmos.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.workflows.DiscoverySyncWorkflow;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync/discovery")
@RequiredArgsConstructor
public class DiscoverySyncController {
    private final DiscoverySyncWorkflow discoverySyncWorkflow;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<DiscoverySyncResponse> discoverySync(@RequestBody @Valid DiscoverySyncRequest discoverySyncRequest) throws JsonProcessingException {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting Synchronization Discovery...", processId);

        var externalEntityIds = discoverySyncRequest.createExternalEntityIdsStringList();
        var localEntitiesIds = discoverySyncWorkflow.discoverySync(processId, discoverySyncRequest.issuer(), externalEntityIds);

        return localEntitiesIds.map(x -> new DiscoverySyncResponse(contextBrokerExternalDomain, x));
    }
}
