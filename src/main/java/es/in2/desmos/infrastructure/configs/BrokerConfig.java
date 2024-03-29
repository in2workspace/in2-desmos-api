package es.in2.desmos.infrastructure.configs;

import es.in2.desmos.infrastructure.configs.properties.BrokerProperties;
import es.in2.desmos.infrastructure.configs.properties.NgsiLdSubscriptionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BrokerConfig {

    private final BrokerProperties brokerProperties;
    private final NgsiLdSubscriptionProperties ngsiLdSubscriptionProperties;

    public String getNotificationEndpoint() {
        return ngsiLdSubscriptionProperties.notificationEndpoint();
    }

    public String getSubscriptionType() {
        return ngsiLdSubscriptionProperties.subscriptionType();
    }

    public String getIdPrefix() {
        return ngsiLdSubscriptionProperties.idPrefix();
    }

    public List<String> getEntityTypes() {
        return ngsiLdSubscriptionProperties.entityTypes();
    }

    public String getEntitiesExternalDomain() {
        return brokerProperties.externalDomain() + brokerProperties.paths().entities();
    }

}
