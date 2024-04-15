package es.in2.desmos.workflows;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.impl.BrokerEntityGetterServiceImpl;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import es.in2.desmos.workflows.impl.P2PDataSyncWorkflowImpl;
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
class P2PDataSyncWorkflowTests {
    @InjectMocks
    private P2PDataSyncWorkflowImpl p2PDataSyncWorkflow;

    @Mock
    private BrokerEntityGetterServiceImpl brokerEntityIdGetterService;

    @Mock
    private DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Test
    void itShouldReturnInternalEntities() {

        List<MVEntity4DataNegotiation> expectedInternalEntities = MVEntity4DataNegotiationMother.list3And4();

        when(brokerEntityIdGetterService.getMvEntities4DataNegotiation()).thenReturn(Mono.just(expectedInternalEntities));

        Mono<List<MVEntity4DataNegotiation>> result = p2PDataSyncWorkflow.dataDiscovery("0", Mono.just("https://example.org"), Mono.just(MVEntity4DataNegotiationMother.list1And2()));

        StepVerifier.create(result)
                .expectNext(expectedInternalEntities)
                .verifyComplete();

        verify(brokerEntityIdGetterService, times(1)).getMvEntities4DataNegotiation();
        verifyNoMoreInteractions(brokerEntityIdGetterService);

    }
}
