package es.in2.desmos.configs;

import es.in2.desmos.configs.properties.DLTAdapterProperties;
import es.in2.desmos.configs.properties.EventSubscriptionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BlockchainConfig {

    private final DLTAdapterProperties dltAdapterProperties;
    private final EventSubscriptionProperties eventSubscriptionProperties;

    public String getNotificationEndpoint() {
        return eventSubscriptionProperties.notificationEndpoint();
    }

    public List<String> getEntityTypes() {
        return eventSubscriptionProperties.eventTypes();
    }

}
