package es.in2.desmos.domain.services.sync.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import es.in2.desmos.domain.models.AccessNodeYamlData;
import es.in2.desmos.domain.services.sync.services.ExternalYamlService;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.cache.AccessNodeMemoryStore;
import es.in2.desmos.infrastructure.configs.properties.AccessNodeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static es.in2.desmos.domain.utils.ApplicationConstants.YAML_FILE_SUFFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalYamlServiceImpl implements ExternalYamlService {

    private final ApiConfig apiConfig;
    private final AccessNodeProperties accessNodeProperties;
    private final Environment env;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final AccessNodeMemoryStore accessNodeMemoryStore;


    @Override
    public Mono<Void> getAccessNodeYamlDataFromExternalSource(String processId) {

        log.debug("ProcessID: {} - Retrieving YAML data from the external source repository...", processId);
        // Get the External URL from configuration
        String repoPath = accessNodeProperties.prefixDirectory() + env.getProperty("SPRING_PROFILES_ACTIVE")  + YAML_FILE_SUFFIX;

        log.debug("ProcessID: {} - External URL: {}", processId, repoPath);
        // Retrieve YAML data from the External URL
        return apiConfig.webClient().get()
                .uri(repoPath)
                .retrieve()
                .bodyToMono(String.class)
                .handle((yamlContent, sink) -> {
                    AccessNodeYamlData data;
                    try {
                        data = yamlMapper.readValue(yamlContent, AccessNodeYamlData.class);
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                        return;
                    }
                    log.debug("ProcessID: {} - AccessNodeYamlData: {}", processId, data);
                    accessNodeMemoryStore.setOrganizations(data);
                });
    }
}
