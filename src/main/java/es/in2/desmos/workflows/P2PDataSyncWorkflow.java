package es.in2.desmos.workflows;

import es.in2.desmos.domain.models.Entity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface P2PDataSyncWorkflow {
    Mono<List<Entity>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<Entity>> externalEntityIds);
}
