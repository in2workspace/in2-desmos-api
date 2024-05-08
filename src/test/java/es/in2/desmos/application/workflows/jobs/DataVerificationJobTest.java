package es.in2.desmos.application.workflows.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.InvalidConsistencyException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.application.workflows.jobs.impl.DataVerificationJobImpl;
import es.in2.desmos.objectmothers.DataNegotiationResultMother;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    void itShouldBuildAnSaveAuditRecord() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.newToSync4AndExistingToSync2();

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample2and4);

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample2().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(any(), any())).thenReturn(Mono.empty());

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new Entity(EntityMother.sample2));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample4().id()), new Entity(EntityMother.sample4));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sample2());
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sample4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sample2().hash(), MVEntity4DataNegotiationMother.sample2().hashlink()));

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), entitySyncResponseMono, Mono.just(existingEntitiesOriginalValidationDataById));

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(auditRecordService, times(2)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq(dataNegotiationResult.issuer()), any(), eq(AuditRecordStatus.RETRIEVED));
        verify(auditRecordService, times(2)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq(dataNegotiationResult.issuer()), any(), eq(AuditRecordStatus.PUBLISHED));
        verifyNoMoreInteractions(auditRecordService);
    }

    @Test
    void itShouldReturnInvalidConsistencyException() throws JSONException {
        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample);

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54fdsafdsadsfdsa").build()));

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new Entity(EntityMother.json2()));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample4().id()), new Entity(EntityMother.json4()));

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
    void itShouldUpsertEntities() {
        String entitySyncResponse = EntitySyncResponseMother.sample2and4;

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample2().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(any(), any())).thenReturn(Mono.empty());

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new Entity(EntityMother.sample2));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample4().id()), new Entity(EntityMother.sample4));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sample2());
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sample4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sample2().hash(), MVEntity4DataNegotiationMother.sample2().hashlink()));

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), Mono.just(entitySyncResponse), Mono.just(existingEntitiesOriginalValidationDataById));


        StepVerifier.
                create(result)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).batchUpsertEntitiesToContextBroker(processId, entitySyncResponse);
        verifyNoMoreInteractions(brokerPublisherService);
    }

    @Test
    void itShouldReturnJsonProcessingExceptionWhenBadJsonInEntitySyncResponse() throws JsonProcessingException {
        String entitySyncResponse = EntitySyncResponseMother.sample;

        String processId = "0";

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        Mono<String> issuer = Mono.just("http://example.org");

        Map<Id, Entity> entitiesById = new HashMap<>();
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new Entity("Invalid json"));
        entitiesById.put(new Id(MVEntity4DataNegotiationMother.sample4().id()), new Entity("Invalid json"));

        List<MVEntity4DataNegotiation> allMVEntity4DataNegotiation = new ArrayList<>();
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sample2());
        allMVEntity4DataNegotiation.add(MVEntity4DataNegotiationMother.sample4());

        Map<Id, HashAndHashLink> existingEntitiesOriginalValidationDataById = new HashMap<>();
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sample2().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sample2().hash(), MVEntity4DataNegotiationMother.sample2().hashlink()));
        existingEntitiesOriginalValidationDataById.put(new Id(MVEntity4DataNegotiationMother.sample4().id()), new HashAndHashLink(MVEntity4DataNegotiationMother.sample4().hash(), MVEntity4DataNegotiationMother.sample4().hashlink()));

        Mono<Void> result = dataVerificationJob.verifyData(processId, issuer, Mono.just(entitiesById), Mono.just(allMVEntity4DataNegotiation), Mono.just(entitySyncResponse), Mono.just(existingEntitiesOriginalValidationDataById));

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof JsonProcessingException)
                .verify();

        verify(objectMapper, times(1)).readTree(anyString());
    }
}