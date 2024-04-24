package es.in2.desmos.domain.services.sync.impl;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
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

    public Mono<String> makeRequest(String processId, Mono<String> issuerMono, Mono<MVEntity4DataNegotiation[]> entitySyncRequest) {
        log.debug("ProcessID: {} - Making a Entity Sync Web Client request", processId);

        return issuerMono.flatMap(issuer -> webClient
                .post()
                .uri(issuer + "/api/v1/sync/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .body(entitySyncRequest, MVEntity4DataNegotiation[].class)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3));
    }
}