package es.in2.desmos.domain.services.broker.adapter.factory;

import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.impl.OrionLdAdapter;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.infrastructure.configs.properties.BrokerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerAdapterFactory {

    private final BrokerProperties brokerPathProperties;
    private final ScorpioAdapter scorpioAdapter;
    private final OrionLdAdapter orionLdAdapter;

    public BrokerAdapterService getBrokerAdapter() {
        return switch (brokerPathProperties.provider()) {
            case "scorpio" -> scorpioAdapter;
            case "orion-ld" -> orionLdAdapter;
            default -> throw new IllegalArgumentException("Invalid IAM provider: " + brokerPathProperties.provider());
        };
    }

}
