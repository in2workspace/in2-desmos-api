package es.in2.desmos.domain.services.sync.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.InvalidIntegrityException;
import es.in2.desmos.domain.exceptions.InvalidSyncResponseException;
import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.sync.EntitySyncWebClient;
import es.in2.desmos.domain.services.sync.jobs.impl.DataTransferJobImpl;
import es.in2.desmos.objectmothers.DataNegotiationResultMother;
import es.in2.desmos.objectmothers.EntitySyncRequestMother;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataTransferJobTest {
    @InjectMocks
    private DataTransferJobImpl dataTransferJob;

    @Mock
    private EntitySyncWebClient entitySyncWebClient;

    @Mock
    private DataVerificationJob dataVerificationJob;

    @SuppressWarnings("CanBeFinal")
    @Spy
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Captor
    private ArgumentCaptor<Mono<String>> monoIssuerCaptor;

    @Captor
    private ArgumentCaptor<Mono<Id[]>> entitySyncRequestCaptor;

    @Captor
    private ArgumentCaptor<Mono<List<MVEntity4DataNegotiation>>> mvEntities4DataNegotiationCaptor;

    @Test
    void itShouldRequestEntitiesToExternalAccessNodeFromMultipleIssuers() {
        String issuer1 = "https://example1.org";
        String issuer2 = "https://example2.org";
        List<DataNegotiationResult> dataNegotiationResults = DataNegotiationResultMother.listNewToSync4AndExistingToSync2(issuer1, issuer2);

        Id[] entitySyncRequest1 = EntitySyncRequestMother.createFromDataNegotiationResult(dataNegotiationResults.get(0));
        Id[] entitySyncRequest2 = EntitySyncRequestMother.createFromDataNegotiationResult(dataNegotiationResults.get(1));

        Mono<String> entitySyncResponseMono2 = Mono.just(EntitySyncResponseMother.sample2);
        Mono<String> entitySyncResponseMono4 = Mono.just(EntitySyncResponseMother.sample4);

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any()))
                .thenAnswer(invocation -> {
                    Mono<String> issuerMono = invocation.getArgument(1);
                    String issuer = issuerMono.block();
                    if (Objects.equals(issuer, issuer1)) {
                        return entitySyncResponseMono2;
                    } else if (Objects.equals(issuer, issuer2)) {
                        return entitySyncResponseMono4;
                    } else {
                        return Mono.empty();
                    }
                });

        when(dataVerificationJob.verifyData(eq(processId), any(), any(), any(), any(), any())).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncDataFromList(processId, Mono.just(dataNegotiationResults));

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(entitySyncWebClient, times(2)).makeRequest(eq(processId), monoIssuerCaptor.capture(), entitySyncRequestCaptor.capture());
        verifyNoMoreInteractions(entitySyncWebClient);

        Mono<String> monoIssuerCaptured1 = monoIssuerCaptor.getAllValues().get(0);

        StepVerifier
                .create(monoIssuerCaptured1)
                .expectNext(dataNegotiationResults.get(0).issuer())
                .verifyComplete();

        Mono<Id[]> entitySyncRequestCaptured1 = entitySyncRequestCaptor.getAllValues().get(0);

        StepVerifier
                .create(entitySyncRequestCaptured1)
                .expectNextMatches(array -> Arrays.equals(entitySyncRequest1, array))
                .verifyComplete();

        Mono<String> monoIssuerCaptured2 = monoIssuerCaptor.getAllValues().get(1);

        StepVerifier
                .create(monoIssuerCaptured2)
                .expectNext(dataNegotiationResults.get(1).issuer())
                .verifyComplete();

        Mono<Id[]> entitySyncRequestCaptured2 = entitySyncRequestCaptor.getAllValues().get(1);

        StepVerifier
                .create(entitySyncRequestCaptured2)
                .expectNextMatches(array -> Arrays.equals(entitySyncRequest2, array))
                .verifyComplete();
    }


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

        when(dataVerificationJob.verifyData(eq(processId), any(), any(), any(), any(), any())).thenReturn(Mono.empty());

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
    }

    @Test
    void itShouldReturnInvalidIntegrityExceptionWhenHashIsIncorrect() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.badHash();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample);

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        when(dataVerificationJob.verifyData(eq(processId), any(), any(), any(), any(), any())).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidIntegrityException &&
                        throwable.getMessage().equals("The hash received at the origin is different from the actual hash of the entity.")
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

        when(dataVerificationJob.verifyData(eq(processId), any(), any(), any(), any(), any())).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .expectErrorMatches(throwable -> throwable instanceof JsonProcessingException)
                .verify();

        verify(objectMapper, times(4)).readTree(anyString());
    }

    @Test
    void itShouldBuildAllMVEntities4DataNegotiation() {
        DataNegotiationResult dataNegotiationResult = DataNegotiationResultMother.sample();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(dataNegotiationResult);

        var allMVEntities4DataNegotiation = Stream.concat(dataNegotiationResult.newEntitiesToSync().stream(), dataNegotiationResult.existingEntitiesToSync().stream()).toList();
        List<MVEntity4DataNegotiation> expectedMVEntities4DataNegotiation = new ArrayList<>(allMVEntities4DataNegotiation);

        Mono<String> entitySyncResponseMono = Mono.just(EntitySyncResponseMother.sample);

        String processId = "0";
        when(entitySyncWebClient.makeRequest(eq(processId), any(), any())).thenReturn(entitySyncResponseMono);

        when(dataVerificationJob.verifyData(eq(processId), any(), any(), any(), any(), any())).thenReturn(Mono.empty());

        Mono<Void> result = dataTransferJob.syncData(processId, dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(dataVerificationJob, times(1)).verifyData(eq(processId), any(), any(), mvEntities4DataNegotiationCaptor.capture(), any(), any());
        verifyNoMoreInteractions(dataVerificationJob);

        Mono<List<MVEntity4DataNegotiation>> monoDataVerificationJobCaptured = mvEntities4DataNegotiationCaptor.getValue();

        StepVerifier
                .create(monoDataVerificationJobCaptured)
                .expectNext(expectedMVEntities4DataNegotiation)
                .verifyComplete();
    }
}