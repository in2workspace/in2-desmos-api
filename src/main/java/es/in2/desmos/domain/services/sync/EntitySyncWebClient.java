package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.Id;
import reactor.core.publisher.Mono;

public interface EntitySyncWebClient {
    Mono<String> makeRequest(String processId, Mono<String> issuer, Mono<Id[]> entitySyncRequest);
}
