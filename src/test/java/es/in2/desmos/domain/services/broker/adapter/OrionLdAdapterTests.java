package es.in2.desmos.domain.services.broker.adapter;

import es.in2.desmos.domain.models.BrokerEntityWithIdTypeLastUpdateAndVersion;
import es.in2.desmos.domain.services.broker.adapter.impl.OrionLdAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class OrionLdAdapterTests {
    @InjectMocks
    private OrionLdAdapter orionLdAdapter;

    @Test
    void itShouldReturnNullWhenGetMvEntities4DataNegotiation() {
        String processId = "0";
        Mono<BrokerEntityWithIdTypeLastUpdateAndVersion[]> result = orionLdAdapter.findAllIdTypeAndAttributesByType(processId, "ProductOffering", "lastUpdate", "version", "lifecycleStatus", "validFor", BrokerEntityWithIdTypeLastUpdateAndVersion[].class);

        assertNull(result);
    }
}