package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DiscoverySyncWebClient {
    Mono<List<MVEntity4DataNegotiation>> makeRequest(String processId, Mono<String> externalAccessNodeMono, Mono<List<MVEntity4DataNegotiation>> localMvEntities4DataNegotiationMono);
}
