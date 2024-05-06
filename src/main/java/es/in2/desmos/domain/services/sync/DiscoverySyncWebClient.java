package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Mono;

public interface DiscoverySyncWebClient {
    Mono<MVEntity4DataNegotiation[]> makeRequest(String processId, Mono<String> externalAccessNodeMono, Mono<MVEntity4DataNegotiation[]> localMvEntities4DataNegotiationMono);
}
