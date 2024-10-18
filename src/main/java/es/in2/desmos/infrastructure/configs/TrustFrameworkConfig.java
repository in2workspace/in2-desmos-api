package es.in2.desmos.infrastructure.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import es.in2.desmos.domain.models.TrustedAccessNodesList;
import es.in2.desmos.infrastructure.configs.properties.AccessNodeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TrustFrameworkConfig {

    private final ApiConfig apiConfig;
    private final AccessNodeProperties accessNodeProperties;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    private final Scheduler scheduler = Schedulers.boundedElastic();
    private final AtomicReference<TrustedAccessNodesList> trustedAccessNodesListRef = new AtomicReference<>();

    @Bean
    public Mono<Void> initialize() {
        log.debug("Retrieving YAML data from the external source repository...");
        // Get the External URL from configuration
        String repoPath = accessNodeProperties.trustedAccessNodesList();

        log.debug("External URL: {}", repoPath);
        // Retrieve YAML data from the External URL

        return getAccessNodesListContent()
                .flatMap(accessNodesListYaml -> deserializeYaml(accessNodesListYaml)
                        .flatMap(accessNodesList -> {
                            save(Mono.just(accessNodesList));
                            return Mono.empty();
                        }));
    }

    @Bean
    public Mono<TrustedAccessNodesList> find() {
        TrustedAccessNodesList currentValue = trustedAccessNodesListRef.get();
        return Mono.justOrEmpty(currentValue);
    }

    private void save(Mono<TrustedAccessNodesList> trustedAccessNodesList){
        trustedAccessNodesList
                .publishOn(scheduler)
                .subscribe(trustedAccessNodesListRef::set);
    }

    private Mono<String> getAccessNodesListContent() {
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

    private Mono<TrustedAccessNodesList> deserializeYaml(String yamlContent) {
        try {
            return Mono.just(yamlMapper.readValue(yamlContent, TrustedAccessNodesList.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException(e));
        }
    }
}
