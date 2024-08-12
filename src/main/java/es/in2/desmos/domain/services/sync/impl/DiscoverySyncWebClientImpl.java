package es.in2.desmos.domain.services.sync.impl;

import com.nimbusds.jose.JOSEException;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.services.sync.DiscoverySyncWebClient;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscoverySyncWebClientImpl implements DiscoverySyncWebClient {
    private final WebClient webClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApiConfig apiConfig;

    @Override
    public Mono<DiscoverySyncResponse> makeRequest(String processId, Mono<String> externalAccessNodeMono, Mono<DiscoverySyncRequest> discoverySyncRequest) {
        log.debug("ProcessID: {} - Making a Discovery Sync Web Client request", processId);

        String token;
        try {
            token = jwtTokenProvider.generateToken("/api/v1/sync/p2p/discovery");
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return externalAccessNodeMono.flatMap(externalAccessNode -> webClient
                .post()
                .uri(externalAccessNode + "/api/v1/sync/p2p/discovery")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("externalNodeUrl", apiConfig.getExternalDomain())
                .contentType(MediaType.APPLICATION_JSON)
                .body(discoverySyncRequest, DiscoverySyncRequest.class)
                .retrieve()
                .bodyToMono(DiscoverySyncResponse.class)
                .retry(3));
    }
}
