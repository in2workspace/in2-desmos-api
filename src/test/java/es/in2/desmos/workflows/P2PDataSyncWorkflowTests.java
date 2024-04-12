package es.in2.desmos.workflows;

import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.workflows.impl.P2PDataSyncWorkflowImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class P2PDataSyncWorkflowTests {
    @InjectMocks
    P2PDataSyncWorkflowImpl p2PDataSyncWorkflow;

    @Test
    void itShouldReturnInternalEntities() {

        List<Entity> expectedInternalEntities = EntityMother.list3And4();

        var result = p2PDataSyncWorkflow.dataDiscovery("0", Mono.just("https://example.org"), Mono.just(EntityMother.list1And2()));

        assertEquals(expectedInternalEntities, result);
    }
}
