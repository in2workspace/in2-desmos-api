package es.in2.desmos.application.workflows.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.application.workflows.jobs.impl.DataVerificationJobImpl;
import es.in2.desmos.domain.exceptions.InvalidConsistencyException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.objectmothers.DataNegotiationResultMother;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataVerificationJobTest {
    @SuppressWarnings("CanBeFinal")
    @Spy
    private static ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private DataVerificationJobImpl dataVerificationJob;
    @Mock
    private AuditRecordService auditRecordService;
    @Mock
    private BrokerPublisherService brokerPublisherService;
    @Captor
    private ArgumentCaptor<Mono<String>> monoIssuerCaptor;

    @Captor
    private ArgumentCaptor<Mono<Id[]>> entitySyncRequestCaptor;

    @Captor
    private ArgumentCaptor<MVEntity4DataNegotiation> mvEntity4DataNegotiationCaptor;


    @Test
    void itShouldBuildAnSaveAuditRecord() throws JsonProcessingException, JSONException {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.newToSync4AndExistingToSync2();

        Mono<String> entitySyncResponseMono = Mono.just(EntityMother.scorpioJson2And4());

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sampleScorpio2().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(any(), any())).thenReturn(Mono.empty());

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new Entity(EntityMother.scorpioDefaultJson2()));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio4().id()), new Entity(EntityMother.scorpioDefaultJson4()));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sampleScorpio2());
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sampleScorpio4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sampleScorpio2().hash(), MVEntity4DataNegotiationMother.sampleScorpio2().hashlink()));

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), entitySyncResponseMono, Mono.just(existingEntitiesOriginalValidationDataById));

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(auditRecordService, times(2)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq(dataNegotiationResult.issuer()), any(), eq(AuditRecordStatus.RETRIEVED));
        verify(auditRecordService, times(2)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq(dataNegotiationResult.issuer()), any(), eq(AuditRecordStatus.PUBLISHED));
        verifyNoMoreInteractions(auditRecordService);
    }

    @Test
    void itShouldBuildAnSaveAuditRecordForSubEntity() throws JsonProcessingException, JSONException {
        Mono<String> entitySyncResponseMono = Mono.just(EntityMother.scorpioJson2And4());

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sampleScorpio2().id())).thenReturn(Mono.just(AuditRecord.builder().entityId(MVEntity4DataNegotiationMother.sampleScorpio2().id()).entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(any(), any())).thenReturn(Mono.empty());

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new Entity(EntityMother.scorpioDefaultJson2()));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio4().id()), new Entity(EntityMother.scorpioDefaultJson4()));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sampleScorpio4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sampleScorpio2().hash(), MVEntity4DataNegotiationMother.sampleScorpio2().hashlink()));

        MVEntity4DataNegotiation expectedMVEntity4DataNegotiationSample4 =
                new MVEntity4DataNegotiation(
                        MVEntity4DataNegotiationMother.sampleScorpio2().id(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().type(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().version(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().lastUpdate(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().lifecycleStatus(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().validFor(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().hash(),
                        "fa54");

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), entitySyncResponseMono, Mono.just(existingEntitiesOriginalValidationDataById));

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(auditRecordService, times(2)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq("http://example.org"), mvEntity4DataNegotiationCaptor.capture(), eq(AuditRecordStatus.RETRIEVED));
        verify(auditRecordService, times(2)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq("http://example.org"), any(), eq(AuditRecordStatus.PUBLISHED));
        verifyNoMoreInteractions(auditRecordService);

        var mvEntity4DataNegotiationSentToAuditRecord = mvEntity4DataNegotiationCaptor.getAllValues();

        assertThat(mvEntity4DataNegotiationSentToAuditRecord.get(0)).isEqualTo(MVEntity4DataNegotiationMother.sampleScorpio4());
        assertThat(mvEntity4DataNegotiationSentToAuditRecord.get(1)).isEqualTo(expectedMVEntity4DataNegotiationSample4);
    }

    @Test
    void itShouldBuildAnSaveAuditRecordForSubEntityWhenNotExistsInAuditRecordDB() throws JsonProcessingException, JSONException {
        Mono<String> entitySyncResponseMono = Mono.just(EntityMother.scorpioJson2And4());

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sampleScorpio2().id()))
                .thenReturn(Mono.just(AuditRecord.builder().entityId(MVEntity4DataNegotiationMother.sampleScorpio2().id()).entityHashLink("fa54").build()))
                .thenReturn(Mono.just(AuditRecord.builder().entityId(MVEntity4DataNegotiationMother.sampleScorpio2().id()).entityId("").entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(any(), any())).thenReturn(Mono.empty());

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new Entity(EntityMother.scorpioDefaultJson2()));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio4().id()), new Entity(EntityMother.scorpioDefaultJson4()));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sampleScorpio4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sampleScorpio2().hash(), MVEntity4DataNegotiationMother.sampleScorpio2().hashlink()));

        MVEntity4DataNegotiation expectedMVEntity4DataNegotiationSample4 =
                new MVEntity4DataNegotiation(
                        MVEntity4DataNegotiationMother.sampleScorpio2().id(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().type(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().version(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().lastUpdate(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().lifecycleStatus(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().validFor(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().hash(),
                        MVEntity4DataNegotiationMother.sampleScorpio2().hash());

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), entitySyncResponseMono, Mono.just(existingEntitiesOriginalValidationDataById));

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(auditRecordService, times(2)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq("http://example.org"), mvEntity4DataNegotiationCaptor.capture(), eq(AuditRecordStatus.RETRIEVED));
        verify(auditRecordService, times(2)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq("http://example.org"), any(), eq(AuditRecordStatus.PUBLISHED));
        verifyNoMoreInteractions(auditRecordService);

        var mvEntity4DataNegotiationSentToAuditRecord = mvEntity4DataNegotiationCaptor.getAllValues();

        assertThat(mvEntity4DataNegotiationSentToAuditRecord.get(0)).isEqualTo(MVEntity4DataNegotiationMother.sampleScorpio4());
        assertThat(mvEntity4DataNegotiationSentToAuditRecord.get(1)).isEqualTo(expectedMVEntity4DataNegotiationSample4);
    }

    @Test
    void itShouldReturnInvalidJsonProcessingExceptionWhenEntityIsInvalid() throws JsonProcessingException, JSONException {
        Mono<String> entitySyncResponseMono = Mono.just(EntityMother.scorpioJson2And4());

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sampleScorpio2().id())).thenReturn(Mono.just(AuditRecord.builder().entityId(MVEntity4DataNegotiationMother.sampleScorpio2().id()).entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(objectMapper.readTree(anyString())).thenThrow(JsonProcessingException.class);

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new Entity(EntityMother.scorpioDefaultJson2()));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio4().id()), new Entity(EntityMother.scorpioDefaultJson4()));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sampleScorpio4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sampleScorpio2().hash(), MVEntity4DataNegotiationMother.sampleScorpio2().hashlink()));

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), entitySyncResponseMono, Mono.just(existingEntitiesOriginalValidationDataById));

        StepVerifier
                .create(result)
                .expectErrorMatches(throwable -> throwable instanceof JsonProcessingException)
                .verify();
    }

    @Test
    void itShouldReturnInvalidConsistencyException() throws JsonProcessingException {
        Mono<String> entitySyncResponseMono = Mono.just(EntityMother.getFullJsonList());

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54fdsafdsadsfdsa").build()));

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new Entity(EntityMother.getJson2()));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample4().id()), new Entity(EntityMother.getJson4()));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sample2());
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sample4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sample2().hash(), MVEntity4DataNegotiationMother.sample2().hashlink()));
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sample4().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sample4().hash(), MVEntity4DataNegotiationMother.sample4().hashlink()));

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), entitySyncResponseMono, Mono.just(existingEntitiesOriginalValidationDataById));

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidConsistencyException &&
                        throwable.getMessage().equals("The hashlink received does not correspond to that of the entity.")
                )
                .verify();
    }

    @Test
    void itShouldUpsertEntities() throws JsonProcessingException, JSONException {
        String entitySyncResponse = EntityMother.scorpioJson2And4();

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sampleScorpio2().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(any(), any())).thenReturn(Mono.empty());

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new Entity(EntityMother.scorpioDefaultJson2()));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio4().id()), new Entity(EntityMother.scorpioDefaultJson4()));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sampleScorpio2());
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sampleScorpio4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sampleScorpio2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sampleScorpio2().hash(), MVEntity4DataNegotiationMother.sampleScorpio2().hashlink()));

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), Mono.just(entitySyncResponse), Mono.just(existingEntitiesOriginalValidationDataById));


        StepVerifier.
                create(result)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).batchUpsertEntitiesToContextBroker(processId, entitySyncResponse);
        verifyNoMoreInteractions(brokerPublisherService);
    }
}