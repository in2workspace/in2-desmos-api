package es.in2.desmos.workflows.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.InvalidConsistencyException;
import es.in2.desmos.domain.exceptions.InvalidIntegrityException;
import es.in2.desmos.domain.exceptions.InvalidSyncResponseException;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.EntitySyncWebClient;
import es.in2.desmos.objectmothers.DataNegotiationResultMother;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import es.in2.desmos.workflows.jobs.impl.DataTransferJobImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
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

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Captor
    private ArgumentCaptor<Mono<String>> monoIssuerCaptor;

    @Captor
    private ArgumentCaptor<Mono<MVEntity4DataNegotiation[]>> entitySyncRequestCaptor;

    @Test
    void itShouldRequestEntitiesToExternalAccessNode() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        MVEntity4DataNegotiation[] entitySyncRequest =
                Stream.concat(
                                dataNegotiationResult.newEntitiesToSync().stream(),
                                dataNegotiationResult.existingEntitiesToSync().stream())
                        .toArray(MVEntity4DataNegotiation[]::new);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample());

        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponseMono);

        String processId = "0";
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample3().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.upsertBatchDataToBroker(any(), any())).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(entitySyncWebClient, times(1)).makeRequest(monoIssuerCaptor.capture(), entitySyncRequestCaptor.capture());
        verifyNoMoreInteractions(entitySyncWebClient);

        Mono<String> monoIssuerCaptured = monoIssuerCaptor.getValue();

        StepVerifier
                .create(monoIssuerCaptured)
                .expectNext(dataNegotiationResult.issuer())
                .verifyComplete();

        Mono<MVEntity4DataNegotiation[]> entitySyncRequestCaptured = entitySyncRequestCaptor.getValue();

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

        MVEntity4DataNegotiation[] entitySyncRequest =
                Stream.concat(
                                dataNegotiationResult.newEntitiesToSync().stream(),
                                dataNegotiationResult.existingEntitiesToSync().stream())
                        .toArray(MVEntity4DataNegotiation[]::new);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample());

        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponseMono);

        String processId = "0";
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample3().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.upsertBatchDataToBroker(any(), any())).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(entitySyncWebClient, times(1)).makeRequest(monoIssuerCaptor.capture(), entitySyncRequestCaptor.capture());
        verifyNoMoreInteractions(entitySyncWebClient);

        Mono<String> monoIssuerCaptured = monoIssuerCaptor.getValue();

        StepVerifier
                .create(monoIssuerCaptured)
                .expectNext(dataNegotiationResult.issuer())
                .verifyComplete();

        Mono<MVEntity4DataNegotiation[]> entitySyncRequestCaptured = entitySyncRequestCaptor.getValue();

        StepVerifier
                .create(entitySyncRequestCaptured)
                .expectNextMatches(array -> Arrays.equals(entitySyncRequest, array))
                .verifyComplete();

        verify(auditRecordService, times(4)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq(dataNegotiationResult.issuer()), any(), any(), eq(AuditRecordStatus.RETRIEVED));
        verify(auditRecordService, times(4)).buildAndSaveAuditRecordFromDataSync(eq(processId), eq(dataNegotiationResult.issuer()), any(), any(), eq(AuditRecordStatus.PUBLISHED));
        verifyNoMoreInteractions(auditRecordService);
    }

    @Test
    void itShouldReturnInvalidIntegrityExceptionWhenHashIsIncorrect() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.badHash();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample());

        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponseMono);

        String processId = "0";
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

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample());

        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponseMono);

        String processId = "0";
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample3().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("jfdlkisajlfdsafjdsafldskisjdfalsda").build()));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

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

        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponseMono);

        String processId = "0";
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

        Mono<String> entitySyncResponseMono = Mono.just("{[]}");

        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponseMono);

        String processId = "0";
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

        String entitySyncResponse = EntitySyncResponseMother.sample();
        Mono<String> entitySyncResponseMono = Mono.just(entitySyncResponse);

        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponseMono);

        String processId = "0";
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample3().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));
        when(auditRecordService.findLatestAuditRecordForEntity(processId, MVEntity4DataNegotiationMother.sample4().id())).thenReturn(Mono.just(AuditRecord.builder().entityHashLink("fa54").build()));

        when(auditRecordService.buildAndSaveAuditRecordFromDataSync(any(), any(), any(), any(), any())).thenReturn(Mono.empty());

        when(brokerPublisherService.upsertBatchDataToBroker(processId, entitySyncResponse)).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(brokerPublisherService, times(1)).upsertBatchDataToBroker(processId, entitySyncResponse);
        verifyNoMoreInteractions(brokerPublisherService);
    }
}