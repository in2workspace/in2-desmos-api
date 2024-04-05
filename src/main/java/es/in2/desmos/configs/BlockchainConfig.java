package es.in2.desmos.configs;

import es.in2.desmos.configs.properties.TxSubscriptionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static es.in2.desmos.domain.utils.ApplicationUtils.getEnvironmentMetadata;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BlockchainConfig {

    private final TxSubscriptionProperties txSubscriptionProperties;
    private final ApiConfig apiConfig;

    public String getNotificationEndpoint() {
        return txSubscriptionProperties.notificationEndpoint();
    }

    public String getMetadata() {
        return getEnvironmentMetadata(apiConfig.getCurrentEnvironment());
    }

    public List<String> getEntityTypes() {
        return txSubscriptionProperties.entityTypes();
    }

}