package es.in2.desmos.workflows;

import es.in2.desmos.domain.models.ProductOffering;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DiscoverySyncWorkflow {
    Mono<List<ProductOffering>> discoverySync(String processId, Mono<String> issuer, Mono<List<String>> externalEntityIds);
}
