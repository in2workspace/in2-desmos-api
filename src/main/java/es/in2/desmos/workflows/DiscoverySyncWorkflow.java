package es.in2.desmos.workflows;

import es.in2.desmos.domain.models.Entity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DiscoverySyncWorkflow {
    Mono<List<Entity>> discoverySync(String processId, Mono<String> issuer, Mono<List<String>> externalEntityIds);
}
