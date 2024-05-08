package es.in2.desmos.application.workflows.jobs;

import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.BrokerEntityWithIdTypeLastUpdateAndVersion;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.api.impl.AuditRecordServiceImpl;
import es.in2.desmos.domain.services.broker.impl.BrokerPublisherServiceImpl;
import es.in2.desmos.domain.services.sync.DiscoverySyncWebClient;
import es.in2.desmos.application.workflows.jobs.impl.P2PDataSyncJobImpl;
import es.in2.desmos.infrastructure.configs.ExternalAccessNodesConfig;
import es.in2.desmos.objectmothers.*;
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
import java.util.Base64;
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

        List<BrokerEntityWithIdTypeLastUpdateAndVersion> brokerEntities = MVBrokerEntity4DataNegotiationMother.list3And4();
        when(brokerPublisherService.findAllIdTypeFirstAttributeAndSecondAttribute(processId, "ProductOffering", "lastUpdate", "version", BrokerEntityWithIdTypeLastUpdateAndVersion[].class)).thenReturn(Mono.just(brokerEntities));


        List<AuditRecord> auditRecordEntities = AuditRecordMother.list3And4();
        when(auditRecordService.findLatestAuditRecordForEntity(processId, auditRecordEntities.get(0).getEntityId())).thenReturn(Mono.just(auditRecordEntities.get(0)));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, auditRecordEntities.get(1).getEntityId())).thenReturn(Mono.just(auditRecordEntities.get(1)));

        MVEntity4DataNegotiation[] sample3InList = new MVEntity4DataNegotiation[]{
                MVEntity4DataNegotiationMother.sample3()
        };
        MVEntity4DataNegotiation[] sample4InList = new MVEntity4DataNegotiation[]{
                MVEntity4DataNegotiationMother.sample4()
        };
        when(discoverySyncWebClient.makeRequest(eq(processId), any(), any()))
                .thenReturn(Mono.just(sample3InList))
                .thenReturn(Mono.just(sample4InList));

        List<String> urlExternalAccessNodesList = UrlMother.example1And2urlsList();
        when(externalAccessNodesConfig.getExternalAccessNodesUrls()).thenReturn(Mono.just(urlExternalAccessNodesList));

        when(dataNegotiationJob.negotiateDataSyncWithMultipleIssuers(eq(processId), any(), any())).thenReturn(Mono.empty());

        var result = p2PDataSyncJob.synchronizeData(processId);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).findAllIdTypeFirstAttributeAndSecondAttribute(processId, "ProductOffering", "lastUpdate", "version", BrokerEntityWithIdTypeLastUpdateAndVersion[].class);
        verifyNoMoreInteractions(brokerPublisherService);

        verify(auditRecordService, times(2)).findLatestAuditRecordForEntity(eq(processId), any());
        verifyNoMoreInteractions(auditRecordService);

        verify(discoverySyncWebClient, times(2)).makeRequest(eq(processId), any(), any());
        verifyNoMoreInteractions(discoverySyncWebClient);

        verify(externalAccessNodesConfig, times(1)).getExternalAccessNodesUrls();
        verifyNoMoreInteractions(externalAccessNodesConfig);

        verify(dataNegotiationJob, times(1)).negotiateDataSyncWithMultipleIssuers(eq(processId), any(), any());
        verifyNoMoreInteractions(dataNegotiationJob);
    }

    @Test
    void itShouldReturnInternalEntities() {

        List<MVEntity4DataNegotiation> expectedInternalEntities = MVEntity4DataNegotiationMother.list3And4();

        List<BrokerEntityWithIdTypeLastUpdateAndVersion> brokerEntities = MVBrokerEntity4DataNegotiationMother.list3And4();

        List<AuditRecord> auditRecordEntities = AuditRecordMother.list3And4();

        String processId = "0";
        when(brokerPublisherService.findAllIdTypeFirstAttributeAndSecondAttribute(processId, "ProductOffering", "lastUpdate", "version", BrokerEntityWithIdTypeLastUpdateAndVersion[].class)).thenReturn(Mono.just(brokerEntities));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, auditRecordEntities.get(0).getEntityId())).thenReturn(Mono.just(auditRecordEntities.get(0)));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, auditRecordEntities.get(1).getEntityId())).thenReturn(Mono.just(auditRecordEntities.get(1)));

        Mono<List<MVEntity4DataNegotiation>> result = p2PDataSyncJob.dataDiscovery(
                processId,
                Mono.just("https://example.org"),
                Mono.just(MVEntity4DataNegotiationMother.list1And2()));

        StepVerifier.create(result)
                .expectNext(expectedInternalEntities)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).findAllIdTypeFirstAttributeAndSecondAttribute(processId, "ProductOffering", "lastUpdate", "version", BrokerEntityWithIdTypeLastUpdateAndVersion[].class);
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

        List<String> expectedLocalEntities = new ArrayList<>();
        for(var item: localEntities){
            String encodedItem = Base64.getEncoder().encodeToString(item.getBytes());

            expectedLocalEntities.add(encodedItem);
        }

        String processId = "0";
        when(brokerPublisherService.findAllById(eq(processId), any())).thenReturn(localEntitiesMono);

        Mono<List<String>> result = p2PDataSyncJob.getLocalEntitiesById(processId, idsMono);

        System.out.println(expectedLocalEntities);

        StepVerifier
                .create(result)
                .expectNext(expectedLocalEntities)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).findAllById(eq(processId), any());
        verifyNoMoreInteractions(brokerPublisherService);
    }
}
