package es.in2.desmos.domain.services.sync.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.InvalidConsistencyException;
import es.in2.desmos.domain.exceptions.InvalidIntegrityException;
import es.in2.desmos.domain.exceptions.InvalidSyncResponseException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.EntitySyncWebClient;
import es.in2.desmos.objectmothers.DataNegotiationResultMother;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import es.in2.desmos.domain.services.sync.jobs.impl.DataTransferJobImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataTransferJobTest {
    @InjectMocks
    private DataTransferJobImpl dataTransferJob;

    @Mock
    private EntitySyncWebClient entitySyncWebClient;

    @Mock
    private AuditRecordService auditRecordService;

    @Mock
    private BrokerPublisherService brokerPublisherService;

    @SuppressWarnings("CanBeFinal")
    @Spy
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Captor
    private ArgumentCaptor<Mono<String>> monoIssuerCaptor;

    @Captor
    private ArgumentCaptor<Mono<Id[]>> entitySyncRequestCaptor;

    @Test
    void itShouldRequestEntitiesToExternalAccessNode() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        Id[] entitySyncRequest =
                Stream.concat(
                                dataNegotiationResult.newEntitiesToSync().stream().map(x -> new Id(x.id())),
                                dataNegotiationResult.existingEntitiesToSync().stream().map(x -> new Id(x.id())))
                        .toArray(Id[]::new);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample);

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample3().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(any(), any())).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(entitySyncWebClient, times(1)).makeRequest(eq(processId), monoIssuerCaptor.capture(), entitySyncRequestCaptor.capture());
        verifyNoMoreInteractions(entitySyncWebClient);

        Mono<String> monoIssuerCaptured = monoIssuerCaptor.getValue();

        StepVerifier
                .create(monoIssuerCaptured)
                .expectNext(dataNegotiationResult.issuer())
                .verifyComplete();

        Mono<Id[]> entitySyncRequestCaptured = entitySyncRequestCaptor.getValue();

        StepVerifier
                .create(entitySyncRequestCaptured)
                .expectNextMatches(array -> Arrays.equals(entitySyncRequest, array))
                .verifyComplete();

        verify(auditRecordService, times(1)).findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample3().id());
        verify(auditRecordService, times(1)).findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id());
        verifyNoMoreInteractions(auditRecordService);
    }

    @Test
    void itShouldBuildAnSaveAuditRecordFromDataSync() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        Id[] entitySyncRequest =
                Stream.concat(
                                dataNegotiationResult.newEntitiesToSync().stream().map(x -> new Id(x.id())),
                                dataNegotiationResult.existingEntitiesToSync().stream().map(x -> new Id(x.id())))
                        .toArray(Id[]::new);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample);

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample3().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(any(), any())).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(entitySyncWebClient, times(1)).makeRequest(eq(processId), monoIssuerCaptor.capture(), entitySyncRequestCaptor.capture());
        verifyNoMoreInteractions(entitySyncWebClient);

        Mono<String> monoIssuerCaptured = monoIssuerCaptor.getValue();

        StepVerifier
                .create(monoIssuerCaptured)
                .expectNext(dataNegotiationResult.issuer())
                .verifyComplete();

        Mono<Id[]> entitySyncRequestCaptured = entitySyncRequestCaptor.getValue();

        StepVerifier
                .create(entitySyncRequestCaptured)
                .expectNextMatches(array -> Arrays.equals(entitySyncRequest, array))
                .verifyComplete();

        verify(auditRecordService, times(4)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq(dataNegotiationResult.issuer()), any(), eq(AuditRecordStatus.RETRIEVED));
        verify(auditRecordService, times(4)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq(dataNegotiationResult.issuer()), any(), eq(AuditRecordStatus.PUBLISHED));
        verifyNoMoreInteractions(auditRecordService);
    }

    @Test
    void itShouldReturnInvalidIntegrityExceptionWhenHashIsIncorrect() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.badHash();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample);

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidIntegrityException &&
                        throwable.getMessage().equals("The hash received at the origin is different from the actual hash of the entity.")
                )
                .verify();
    }

    @Test
    void itShouldReturnInvalidConsistencyException() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample);

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("jfdlkisajlfdsafjdsafldskisjdfalsda").build()));

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidConsistencyException &&
                        throwable.getMessage().equals("The hashlink received does not correspond to that of the entity.")
                )
                .verify();
    }

    @Test
    void itShouldReturnBadEntitySyncResponseExceptionWhenSyncResponseJsonHasNotArray() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        Mono<String> entitySyncResponseMono = Mono.just("{}");

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidSyncResponseException &&
                        throwable.getMessage().equals("Invalid EntitySync response.")
                )
                .verify();
    }

    @Test
    void itShouldReturnInvalidSyncResponseExceptionWhenJsonArrayHasNotIdField() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        Mono<String> entitySyncResponseMono = Mono.just("{}");

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidSyncResponseException &&
                        throwable.getMessage().equals("Invalid EntitySync response.")
                )
                .verify();
    }

    @Test
    void itShouldUpsertEntities() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        String entitySyncResponse = EntitySyncResponseMother.sample;
        Mono<String> entitySyncResponseMono = Mono.just(entitySyncResponse);

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample3().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.batchUpsertEntitiesToContextBroker(processId, entitySyncResponse)).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).batchUpsertEntitiesToContextBroker(processId, entitySyncResponse);
        verifyNoMoreInteractions(brokerPublisherService);
    }

    @Test
    void itShouldReturnJsonProcessingExceptionWhenBadJsonInEntitySyncResponse() throws JsonProcessingException {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        String processId = "0";
        Mono<String> entitySyncResponseMono = Mono.just("invalid json");

        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof JsonProcessingException)
                .verify();

        verify(objectMapper, times(1)).readTree(anyString());
    }

    @Test
    void itShouldReturnJsonProcessingExceptionWhenBadJsonSortingEntities() throws JsonProcessingException {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        String processId = "0";
        String entitySyncResponse = EntitySyncResponseMother.sample;
        Mono<String> entitySyncResponseMono = Mono.just(entitySyncResponse);
        JsonNode entitySyncResponseJsonNode = objectMapper.readTree(entitySyncResponse);

        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);
        when(objectMapper.readTree(anyString())).thenReturn(entitySyncResponseJsonNode).thenThrow(JsonProcessingException.class);

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof JsonProcessingException)
                .verify();

        verify(objectMapper, times(4)).readTree(anyString());
    }
}