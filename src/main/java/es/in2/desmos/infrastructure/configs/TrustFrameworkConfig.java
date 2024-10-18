package es.in2.desmos.infrastructure.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TrustFrameworkConfig {

    private final ApiConfig apiConfig;
    private final AccessNodeProperties accessNodeProperties;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    private final Scheduler scheduler = Schedulers.boundedElastic();

    private final AtomicReference<HashMap<String, String>> publicKeysByUrlRef = new AtomicReference<>();

    private final AtomicReference<HashSet<String>> dltAddressesRef = new AtomicReference<>();

    @Bean
    public Mono<Void> initialize() {
        log.debug("Retrieving YAML data from the external source repository...");
        // Get the External URL from configuration
        String repoPath = accessNodeProperties.trustedAccessNodesList();

        log.debug("External URL: {}", repoPath);
        // Retrieve YAML data from the External URL

        return getAccessNodesListContent()
                .flatMap(yamlAccessNodesList -> {
                    HashMap<String, String> publicKeysByUrl = deserializePublicKeysByUrl(yamlAccessNodesList);
                    savePublicKeysByUrlRef(Mono.just(publicKeysByUrl));

                    HashSet<String> dltAddresses = deserializeDltAddress(yamlAccessNodesList);
                    saveDltAddressesRef(Mono.just(dltAddresses));
                    return Mono.empty();
                });
    }

    @Bean
    public Mono<HashMap<String, String>> publicKeysByUrl() {
        HashMap<String, String> publicKeysByUrl = publicKeysByUrlRef.get();
        return Mono.justOrEmpty(publicKeysByUrl);
    }

    @Bean
    public Mono<HashSet<String>> getDltAddresses() {
        HashSet<String> dltAddresses = dltAddressesRef.get();
        return Mono.justOrEmpty(dltAddresses);
    }

    private void savePublicKeysByUrlRef(Mono<HashMap<String, String>> publicKeysByUrl) {
        publicKeysByUrl
                .publishOn(scheduler)
                .subscribe(publicKeysByUrlRef::set);
    }

    private void saveDltAddressesRef(Mono<HashSet<String>> dltAddresses) {
        dltAddresses
                .publishOn(scheduler)
                .subscribe(dltAddressesRef::set);
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

    private HashMap<String, String> deserializePublicKeysByUrl(String yamlContent) {
        try {
            var data = yamlMapper.readValue(yamlContent, new TypeReference<Map<String, Object>>() {
            });

            List<Map<String, String>> organizations = yamlMapper.convertValue(data.get("organizations"),
                    new TypeReference<>() {
                    });
            HashMap<String, String> resultMap = new HashMap<>();

            for (Map<String, String> node : organizations) {
                String url = node.get("url");
                String publicKey = node.get("publicKey");
                resultMap.put(url, publicKey);
            }

            return resultMap;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HashSet<String> deserializeDltAddress(String yamlContent) {
        try {
            var data = yamlMapper.readValue(yamlContent, new TypeReference<Map<String, Object>>() {
            });

            List<Map<String, String>> organizations = yamlMapper.convertValue(data.get("organizations"),
                    new TypeReference<>() {
                    });
            HashSet<String> resultSet = new HashSet<>();

            for (Map<String, String> node : organizations) {
                String dltAddress = node.get("dlt_address");
                resultSet.add(dltAddress);
            }

            return resultSet;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
