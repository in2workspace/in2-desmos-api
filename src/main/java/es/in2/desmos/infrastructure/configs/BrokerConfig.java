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

    public List<String> getEntityTypes() {
        return ngsiLdSubscriptionProperties.entityTypes();
    }

    public String getEntitiesExternalDomain() { return brokerProperties.internalDomain() + brokerProperties.paths().entities();}

    public String getEntitiesPath() {
        return brokerProperties.paths().entities();
    }

    public String getEntityOperationsPath() {
        return brokerProperties.paths().entityOperations();
    }

    public String getSubscriptionsPath() {
        return brokerProperties.paths().subscriptions();
    }

    public String getTemporalPath() {
        return brokerProperties.paths().temporal();
    }

    public String getInternalDomain() { return brokerProperties.internalDomain(); }

}
