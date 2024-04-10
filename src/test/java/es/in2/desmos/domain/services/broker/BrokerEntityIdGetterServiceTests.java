package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.impl.BrokerEntityIdGetterServiceImpl;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerEntityIdGetterServiceTests {

    @InjectMocks
    private BrokerEntityIdGetterServiceImpl brokerEntityIdGetterService;

    @Mock
    BrokerAdapterService brokerAdapterService;

    @Test
    void itShouldReturnEntityId() {

        Mono<List<ProductOffering>> productOfferings = Mono.just(ProductOfferingMother.fullList());
        when(brokerAdapterService.getEntityIds()).thenReturn(productOfferings);

        Mono<List<ProductOffering>> result = brokerEntityIdGetterService.getData();

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        Assertions.assertEquals(result, productOfferings);

        verify(brokerAdapterService, times(1)).getEntityIds();
        verifyNoMoreInteractions(brokerAdapterService);
    }
}
