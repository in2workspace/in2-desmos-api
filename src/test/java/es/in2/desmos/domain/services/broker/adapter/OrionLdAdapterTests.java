package es.in2.desmos.domain.services.broker.adapter;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.adapter.impl.OrionLdAdapter;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class OrionLdAdapterTests {
    @InjectMocks
    private OrionLdAdapter orionLdAdapter;

    @Test
    void itShouldReturnNullWhenGetMvEntities4DataNegotiation() {
        Mono<List<MVEntity4DataNegotiation>> result = orionLdAdapter.getMvEntities4DataNegotiation();

        assertNull(result);
    }

    @Test
    void itShouldReturnNullWhenBatchPostEntities() {
        String processId = "0";
        String requestBody = EntitySyncResponseMother.sample();

        Mono<Void> result = orionLdAdapter.batchPostEntities(processId, requestBody);

        assertNull(result);
    }
}