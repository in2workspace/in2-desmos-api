package es.in2.desmos.workflows;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.services.broker.impl.BrokerEntityIdGetterServiceImpl;
import es.in2.desmos.objectmothers.EntityMother;
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
    private BrokerEntityIdGetterServiceImpl brokerEntityIdGetterService;

    @Mock
    private DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Test
    void itShouldReturnInternalEntities() {

        List<Entity> expectedInternalEntities = EntityMother.list3And4();

        when(brokerEntityIdGetterService.getData()).thenReturn(Mono.just(expectedInternalEntities));

        Mono<List<Entity>> result = p2PDataSyncWorkflow.dataDiscovery("0", Mono.just("https://example.org"), Mono.just(EntityMother.list1And2()));

        StepVerifier.create(result)
                .expectNext(expectedInternalEntities)
                .verifyComplete();

        verify(brokerEntityIdGetterService, times(1)).getData();
        verifyNoMoreInteractions(brokerEntityIdGetterService);

    }
}
