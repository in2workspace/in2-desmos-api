package es.in2.desmos.domain.services.sync.impl;

import es.in2.desmos.domain.models.EntitySyncRequest;
import es.in2.desmos.domain.models.EntitySyncResponse;
import es.in2.desmos.domain.services.sync.EntitySyncWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntitySyncWebClientImpl implements EntitySyncWebClient {
    private final WebClient webClient;

    public Mono<EntitySyncResponse> makeRequest(Mono<String> issuer, Mono<EntitySyncRequest> entitySyncRequest) {
        return webClient
                .post()
                .uri(issuer + "/api/v1/sync/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .body(entitySyncRequest,EntitySyncRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(EntitySyncResponse::new);
    }
}