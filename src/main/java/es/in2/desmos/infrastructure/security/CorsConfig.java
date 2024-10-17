package es.in2.desmos.infrastructure.security;

import es.in2.desmos.domain.models.TrustedAccessNode;
import es.in2.desmos.domain.models.TrustedAccessNodesList;
import es.in2.desmos.domain.repositories.TrustedAccessNodesListRepository;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
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
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final TrustedAccessNodesListRepository trustedAccessNodesListRepository;
    private final AccessNodeProperties accessNodeProperties;
    private final BrokerConfig brokerConfig;
    private final DLTAdapterProperties dltAdapterProperties;

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
        String repoPath = accessNodeProperties.trustedAccessNodesList();

        log.debug("External URL: {}", repoPath);
        // Retrieve YAML data from the External URL

        List<String> urls = new ArrayList<>();
        TrustedAccessNodesList yamlData = trustedAccessNodesListRepository.getTrustedAccessNodeList().block();
        if (yamlData == null || yamlData.getOrganizations() == null) {
            log.warn("No organizations data available in AccessNodeMemoryStore.");
            return urls;
        }
        for (TrustedAccessNode org : yamlData.getOrganizations()) {
            urls.add(org.getUrl());
        }

        return urls;
    }
}
