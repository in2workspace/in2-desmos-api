package es.in2.desmos.workflows.jobs;

import es.in2.desmos.configs.ExternalAccessNodesConfig;
import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.api.impl.AuditRecordServiceImpl;
import es.in2.desmos.domain.services.broker.impl.BrokerPublisherServiceImpl;
import es.in2.desmos.domain.services.sync.DiscoverySyncWebClient;
import es.in2.desmos.objectmothers.*;
import es.in2.desmos.workflows.jobs.impl.P2PDataSyncJobImpl;
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
    private P2PDataSyncJobImpl p2PDataSyncJob;

    @Mock
    private ExternalAccessNodesConfig externalAccessNodesConfig;

    @Mock
    private BrokerPublisherServiceImpl brokerPublisherService;

    @Mock
    private AuditRecordServiceImpl auditRecordService;

    @Mock
    private DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Mock
    private DiscoverySyncWebClient discoverySyncWebClient;

    @Mock
    private DataNegotiationJob dataNegotiationJob;

    @Test
    void itShouldUpsertEntitiesFromOtherAccessNodes(){
        String processId = "0";

        List<MVBrokerEntity4DataNegotiation> brokerEntities = MVBrokerEntity4DataNegotiationMother.list3And4();
        when(brokerPublisherService.getMVBrokerEntities4DataNegotiation(processId, "ProductOffering", "lastUpdate", "version")).thenReturn(Mono.just(brokerEntities));


        List<AuditRecord> auditRecordEntities = AuditRecordMother.list3And4();
        when(auditRecordService.findLatestAuditRecordForEntity(processId, auditRecordEntities.get(0).getEntityId())).thenReturn(Mono.just(auditRecordEntities.get(0)));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, auditRecordEntities.get(1).getEntityId())).thenReturn(Mono.just(auditRecordEntities.get(1)));

        List<MVEntity4DataNegotiation> sample3InList = new ArrayList<>();
        sample3InList.add(MVEntity4DataNegotiationMother.sample3());
        List<MVEntity4DataNegotiation> sample4InList = new ArrayList<>();
        sample3InList.add(MVEntity4DataNegotiationMother.sample4());
        when(discoverySyncWebClient.makeRequest(eq(processId), any(), any()))
                .thenReturn(Mono.just(sample3InList))
                .thenReturn(Mono.just(sample4InList));

        List<String> urlExternalAccessNodesList = new ArrayList<>();
        urlExternalAccessNodesList.add("https://example1.org");
        urlExternalAccessNodesList.add("https://example2.org");
        when(externalAccessNodesConfig.getExternalAccessNodesUrls()).thenReturn(Mono.just(urlExternalAccessNodesList));

        when(dataNegotiationJob.negotiateDataSync(eq(processId), any(), any())).thenReturn(Mono.empty());

        var result = p2PDataSyncJob.synchronizeData(processId);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).getMVBrokerEntities4DataNegotiation(processId, "ProductOffering", "lastUpdate", "version");
        verifyNoMoreInteractions(brokerPublisherService);

        verify(auditRecordService, times(2)).findLatestAuditRecordForEntity(eq(processId), any());
        verifyNoMoreInteractions(auditRecordService);

        verify(discoverySyncWebClient, times(2)).makeRequest(eq(processId), any(), any());
        verifyNoMoreInteractions(discoverySyncWebClient);

        verify(externalAccessNodesConfig, times(1)).getExternalAccessNodesUrls();
        verifyNoMoreInteractions(externalAccessNodesConfig);

        verify(dataNegotiationJob, times(1)).negotiateDataSync(eq(processId), any(), any());
        verifyNoMoreInteractions(dataNegotiationJob);
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

        Mono<List<MVEntity4DataNegotiation>> result = p2PDataSyncJob.dataDiscovery(
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

        Mono<List<String>> result = p2PDataSyncJob.getLocalEntitiesById(processId, idsMono);

        StepVerifier
                .create(result)
                .expectNext(localEntities)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).findAllById(eq(processId), any());
        verifyNoMoreInteractions(brokerPublisherService);
    }
}
