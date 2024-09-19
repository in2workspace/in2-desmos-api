package es.in2.desmos.infrastructure.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import es.in2.desmos.domain.exceptions.InvalidProfileException;
import es.in2.desmos.domain.models.AccessNodeOrganization;
import es.in2.desmos.domain.models.AccessNodeYamlData;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import es.in2.desmos.infrastructure.configs.cache.AccessNodeMemoryStore;
import es.in2.desmos.infrastructure.configs.properties.AccessNodeProperties;
import es.in2.desmos.infrastructure.configs.properties.DLTAdapterProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

import static es.in2.desmos.domain.utils.ApplicationConstants.YAML_FILE_SUFFIX;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final AccessNodeMemoryStore accessNodeMemoryStore;
    private final AccessNodeProperties accessNodeProperties;
    private final ApiConfig apiConfig;
    private final BrokerConfig brokerConfig;
    private final DLTAdapterProperties dltAdapterProperties;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration brokerCorsConfig = new CorsConfiguration();
        setBrokerCorsConfig(brokerCorsConfig, List.of(brokerConfig.getInternalDomain()), List.of(
                HttpMethod.POST.name()), false);
        source.registerCorsConfiguration("/api/v1/notifications/broker", brokerCorsConfig);

        CorsConfiguration dltAdapterCorsConfig = new CorsConfiguration();
        setBrokerCorsConfig(dltAdapterCorsConfig, List.of(dltAdapterProperties.internalDomain(),
                dltAdapterProperties.externalDomain()), List.of(HttpMethod.POST.name()), false);
        source.registerCorsConfiguration("/api/v1/notifications/dlt", dltAdapterCorsConfig);


        CorsConfiguration githubSyncUrlsCorsConfig = new CorsConfiguration();
        setBrokerCorsConfig(githubSyncUrlsCorsConfig, getCorsUrls(), List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name()), true);
        source.registerCorsConfiguration("/api/v1/sync/p2p/**", githubSyncUrlsCorsConfig);

        CorsConfiguration githubEntitiesUrlsCorsConfig = new CorsConfiguration();
        setBrokerCorsConfig(githubEntitiesUrlsCorsConfig, getCorsUrls(), List.of(
                HttpMethod.GET.name()), true);
        source.registerCorsConfiguration("/api/v1/entities/**", githubEntitiesUrlsCorsConfig);

        return source;
    }

    private void setBrokerCorsConfig(CorsConfiguration brokerCorsConfig, List<String> allowedOrigins, List<String> allowedMethods, boolean allowCredentials) {
        brokerCorsConfig.setAllowedOrigins(allowedOrigins);
        brokerCorsConfig.setMaxAge(8000L);
        brokerCorsConfig.setAllowedMethods(allowedMethods);
        brokerCorsConfig.addAllowedHeader("*");
        brokerCorsConfig.addExposedHeader("*");
        brokerCorsConfig.setAllowCredentials(allowCredentials);
    }

    private List<String> getCorsUrls() {

        log.debug("Retrieving YAML data from the external source repository...");
        // Get the External URL from configuration
        String repoPath = accessNodeProperties.prefixDirectory() + getExternalYamlProfile() + YAML_FILE_SUFFIX;

        log.debug("External URL: {}", repoPath);
        // Retrieve YAML data from the External URL

        apiConfig.webClient().get()
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
                    log.debug("AccessNodeYamlData: {}", data);
                    accessNodeMemoryStore.setOrganizations(data);
                }).block();


        List<String> urls = new ArrayList<>();
        AccessNodeYamlData yamlData = accessNodeMemoryStore.getOrganizations();
        if (yamlData == null || yamlData.getOrganizations() == null) {
            log.warn("No organizations data available in AccessNodeMemoryStore.");
            return urls;
        }
        for (AccessNodeOrganization org : yamlData.getOrganizations()) {
            urls.add(org.getUrl());
        }

        return urls;
    }

    private String getExternalYamlProfile() {
        String profile = apiConfig.getCurrentEnvironment();

        if (profile == null) {
            throw new InvalidProfileException("Environment variable SPRING_PROFILES_ACTIVE is not set");
        }

        return switch (profile) {
            case "default", "dev" -> "sbx";
            case "test" -> "dev";
            case "prod" -> "prd";
            default -> throw new InvalidProfileException("Invalid profile: " + profile);
        };
    }
}
