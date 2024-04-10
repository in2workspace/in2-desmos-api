package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.EntitySyncRequest;
import es.in2.desmos.domain.models.EntitySyncResponse;
import reactor.core.publisher.Mono;

public interface EntitySyncWebClient {
    Mono<EntitySyncResponse> makeRequest(Mono<String> issuer, Mono<EntitySyncRequest> entitySyncRequest);
}
