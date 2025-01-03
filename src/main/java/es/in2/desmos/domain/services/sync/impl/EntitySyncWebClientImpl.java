package es.in2.desmos.domain.services.sync.impl;

import com.nimbusds.jose.JOSEException;
import es.in2.desmos.domain.exceptions.InvalidTokenException;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.services.sync.EntitySyncWebClient;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntitySyncWebClientImpl implements EntitySyncWebClient {
    private final WebClient webClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApiConfig apiConfig;

    public Flux<String> makeRequest(String processId, Mono<String> issuerMono, Mono<Id[]> entitySyncRequest) {
        log.info("ProcessID: {} - Making a Entity Sync Web Client request", processId);


        String token;
        try {
            token = jwtTokenProvider.generateToken("/api/v1/sync/p2p/entities");
        } catch (JOSEException e) {
            throw new InvalidTokenException(e.getMessage());
        }

        return issuerMono.flatMapMany(issuer -> webClient
                .post()
                .uri(issuer + "/api/v1/sync/p2p/entities")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("external-node-url", apiConfig.getExternalDomain())
                .contentType(MediaType.APPLICATION_JSON)
                .body(entitySyncRequest, Id[].class)
                .retrieve()
                .bodyToFlux(Entity.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .flatMap(x -> Flux.just(x.value())));
    }
}