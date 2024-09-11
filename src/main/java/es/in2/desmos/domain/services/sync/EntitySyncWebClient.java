package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EntitySyncWebClient {
    Flux<String> makeRequest(String processId, Mono<String> issuer, Mono<Id[]> entitySyncRequest);
}
