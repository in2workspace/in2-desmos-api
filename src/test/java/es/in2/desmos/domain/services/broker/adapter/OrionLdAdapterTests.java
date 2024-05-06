package es.in2.desmos.domain.services.broker.adapter;

import es.in2.desmos.domain.models.BrokerEntityWithIdTypeLastUpdateAndVersion;
import es.in2.desmos.domain.services.broker.adapter.impl.OrionLdAdapter;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
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
        Mono<BrokerEntityWithIdTypeLastUpdateAndVersion[]> result = orionLdAdapter.findAllIdTypeFirstAttributeAndSecondAttribute(processId, "ProductOffering", "lastUpdate", "version", BrokerEntityWithIdTypeLastUpdateAndVersion[].class);

        assertNull(result);
    }

    @Test
    void itShouldReturnNullWhenBatchUpsertEntities() {
        String processId = "0";
        String requestBody = EntitySyncResponseMother.sample;

        Mono<Void> result = orionLdAdapter.batchUpsertEntities(processId, requestBody);

        assertNull(result);
    }
}