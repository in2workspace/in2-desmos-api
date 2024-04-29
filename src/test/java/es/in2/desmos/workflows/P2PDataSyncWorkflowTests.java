package es.in2.desmos.workflows;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.api.impl.AuditRecordServiceImpl;
import es.in2.desmos.domain.services.broker.impl.BrokerPublisherServiceImpl;
import es.in2.desmos.objectmothers.AuditRecordMother;
import es.in2.desmos.objectmothers.MVBrokerEntity4DataNegotiationMother;
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
    private BrokerPublisherServiceImpl brokerPublisherService;

    @Mock
    private AuditRecordServiceImpl auditRecordService;

    @Mock
    private DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Test
    void itShouldReturnInternalEntities() {

        List<MVEntity4DataNegotiation> expectedInternalEntities = MVEntity4DataNegotiationMother.list3And4();

        List<MVBrokerEntity4DataNegotiation> brokerEntities = MVBrokerEntity4DataNegotiationMother.list3And4();

        List<AuditRecord> auditRecordEntities = AuditRecordMother.list3And4();

        String processId = "0";
        when(brokerPublisherService.getMVBrokerEntities4DataNegotiation(processId, "ProductOffering", "lastUpdate", "version")).thenReturn(Mono.just(brokerEntities));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, auditRecordEntities.get(0).getEntityId())).thenReturn(Mono.just(auditRecordEntities.get(0)));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, auditRecordEntities.get(1).getEntityId())).thenReturn(Mono.just(auditRecordEntities.get(1)));

        Mono<List<MVEntity4DataNegotiation>> result = p2PDataSyncWorkflow.dataDiscovery(
                processId,
                Mono.just("https://example.org"),
                Mono.just(MVEntity4DataNegotiationMother.list1And2()));

        StepVerifier.create(result)
                .expectNext(expectedInternalEntities)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).getMVBrokerEntities4DataNegotiation(processId, "ProductOffering", "lastUpdate", "version");
        verifyNoMoreInteractions(brokerPublisherService);

    }
}
