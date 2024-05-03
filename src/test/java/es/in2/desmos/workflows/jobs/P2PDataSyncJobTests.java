package es.in2.desmos.workflows.jobs;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.api.impl.AuditRecordServiceImpl;
import es.in2.desmos.domain.services.broker.impl.BrokerPublisherServiceImpl;
import es.in2.desmos.domain.services.sync.jobs.DataNegotiationJob;
import es.in2.desmos.objectmothers.*;
import es.in2.desmos.domain.services.sync.jobs.impl.P2PDataSyncJobImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class P2PDataSyncJobTests {
    @InjectMocks
    private P2PDataSyncJobImpl p2PDataSyncWorkflow;

    @Mock
    private BrokerPublisherServiceImpl brokerPublisherService;

    @Mock
    private AuditRecordServiceImpl auditRecordService;

    @Mock
    private DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Mock
    private DataNegotiationJob dataNegotiationJob;

    @Test
    void itShouldUpsertEntitiesFromOtherAccessNodes(){
        String processId = "0";

        when(dataNegotiationJob.negotiateDataSync()).thenReturn(Mono.empty());

        var result = p2PDataSyncWorkflow.synchronizeData(processId);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(dataNegotiationJob, times(1)).negotiateDataSync();
        verifyNoMoreInteractions(brokerPublisherService);
    }

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

    @Test
    void itShouldReturnLocalEntitiesWhenPassingId() throws JSONException {
        Mono<List<Id>> idsMono = Mono.just(Arrays.stream(IdMother.entitiesRequest).toList());

        String entityRequestBrokerJson = BrokerDataMother.getEntityRequestBrokerJson;
        JSONArray expectedResponseJsonArray = new JSONArray(entityRequestBrokerJson);
        List<String> localEntities = new ArrayList<>();
        for (int i = 0; i < expectedResponseJsonArray.length(); i++) {
            String entity = expectedResponseJsonArray.getString(i);
            localEntities.add(entity);
        }
        Mono<List<String>> localEntitiesMono = Mono.just(localEntities);
        String processId = "0";
        when(brokerPublisherService.findAllById(eq(processId), any())).thenReturn(localEntitiesMono);

        Mono<List<String>> result = p2PDataSyncWorkflow.getLocalEntitiesById(processId, idsMono);

        StepVerifier
                .create(result)
                .expectNext(localEntities)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).findAllById(eq(processId), any());
        verifyNoMoreInteractions(brokerPublisherService);
    }
}
