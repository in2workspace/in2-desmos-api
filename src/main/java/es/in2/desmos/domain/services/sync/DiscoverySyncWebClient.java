package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import reactor.core.publisher.Mono;

public interface DiscoverySyncWebClient {
    Mono<DiscoverySyncResponse> makeRequest(String processId, Mono<String> externalAccessNodeMono, Mono<DiscoverySyncRequest> localMvEntities4DataNegotiationMono);
}
