package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Mono;

public interface EntitySyncWebClient {
    Mono<String> makeRequest(Mono<String> issuer, Mono<MVEntity4DataNegotiation[]> entitySyncRequest);
}
