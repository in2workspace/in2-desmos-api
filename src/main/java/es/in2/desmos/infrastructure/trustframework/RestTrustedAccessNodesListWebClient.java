package es.in2.desmos.infrastructure.trustframework;

import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.properties.AccessNodeProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class RestTrustedAccessNodesListWebClient {
    private final ApiConfig apiConfig;
    private final AccessNodeProperties accessNodeProperties;

    public Mono<String> getAccessNodesListContent() {
        try {
            URI trustedAccessNodesListUri = new URI(accessNodeProperties.trustedAccessNodesList());
            return apiConfig
                    .webClient()
                    .get()
                    .uri(trustedAccessNodesListUri)
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (URISyntaxException e) {
            return Mono.error(new RuntimeException(e));
        }
    }
}
