package es.in2.desmos.domain.services.sync.impl;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.sync.DiscoverySyncWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscoverySyncWebClientImpl implements DiscoverySyncWebClient {
    private final WebClient webClient;

    @Override
    public Mono<MVEntity4DataNegotiation[]> makeRequest(String processId, Mono<String> externalAccessNodeMono, Mono<MVEntity4DataNegotiation[]> localMvEntities4DataNegotiationMono) {
        log.debug("ProcessID: {} - Making a Entity Sync Web Client request", processId);

        return externalAccessNodeMono.flatMap(externalAccessNode -> webClient
                .post()
                .uri(externalAccessNode + "/api/v1/sync/p2p/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .body(localMvEntities4DataNegotiationMono, MVEntity4DataNegotiation[].class)
                .retrieve()
                .bodyToMono(MVEntity4DataNegotiation[].class)
                .retry(3));
    }
}
