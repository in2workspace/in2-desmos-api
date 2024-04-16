package es.in2.desmos.workflows.jobs;

import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.EntitySyncRequest;
import es.in2.desmos.domain.models.EntitySyncResponse;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.sync.EntitySyncWebClient;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import es.in2.desmos.workflows.jobs.impl.DataTransferJobImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataTransferJobTest {
    @InjectMocks
    private DataTransferJobImpl dataTransferJob;

    @Mock
    private EntitySyncWebClient entitySyncWebClient;

    @Mock
    private AuditRecordService auditRecordService;

    @Captor
    private ArgumentCaptor<Mono<String>> issuerCaptor;

    @Captor
    private ArgumentCaptor<Mono<EntitySyncRequest>> entitySyncRequestCaptor;

    /*@Test
    void itShouldRequestEntitiesToExternalAccessNodeAndSaveToAuditRecord() throws JsonProcessingException {
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(DataNegotiationResultMother.sample());

        List<MVEntity4DataNegotiation> allEntitiesToSync = MVEntity4DataNegotiationMother.fullList();
        Mono<EntitySyncRequest> entitySyncRequestMono = Mono.just(new EntitySyncRequest(allEntitiesToSync));
        ObjectMapper objectMapper = new ObjectMapper();
        String entitiesResponse = objectMapper.writeValueAsString(entitySyncRequestMono);
        Mono<EntitySyncResponse> entitySyncResponse = Mono.just(new EntitySyncResponse(entitiesResponse));

        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponse);

        when(auditRecordService.findLatestAuditRecordForEntity(any(), any())).thenReturn();

        Mono<Void> result = dataTransferJob.syncData(dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();

        verify(entitySyncWebClient, times(1)).makeRequest(issuerCaptor.capture(), entitySyncRequestCaptor.capture());
        verifyNoMoreInteractions(entitySyncWebClient);

        Mono<String> issuerCaptured = issuerCaptor.getValue();

        StepVerifier
                .create(issuerCaptured)
                .expectNext(issuer)
                .verifyComplete();

        Mono<EntitySyncRequest> entitySyncRequestCaptured = entitySyncRequestCaptor.getValue();

        StepVerifier
                .create(entitySyncRequestCaptured)
                .expectNext(entitySyncRequest)
                .verifyComplete();


    }*/

    @Test
    void itShouldValidateEntities() throws JsonProcessingException {
        String issuer = "http://example.org";
        List<MVEntity4DataNegotiation> newEntitiesToSync = MVEntity4DataNegotiationMother.list1And2();
        List<MVEntity4DataNegotiation> existingEntitiesToSync = MVEntity4DataNegotiationMother.list3And4();
        Mono<DataNegotiationResult> dataNegotiationResultMono = Mono.just(new DataNegotiationResult(issuer, newEntitiesToSync, existingEntitiesToSync));

        List<MVEntity4DataNegotiation> entities =
                Stream.concat(
                                newEntitiesToSync
                                        .stream(),
                                existingEntitiesToSync
                                        .stream())
                        .toList();

        EntitySyncRequest entitySyncRequest = new EntitySyncRequest(entities);
        Mono<EntitySyncRequest> entitySyncRequestMono = Mono.just(entitySyncRequest);

        ObjectMapper objectMapper = new ObjectMapper();

        String entitiesResponse = objectMapper.writeValueAsString(entitySyncRequestMono);

        Mono<EntitySyncResponse> entitySyncResponse = Mono.just(new EntitySyncResponse(entitiesResponse));
        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponse);

        Mono<Void> result = dataTransferJob.syncData(dataNegotiationResultMono);

        StepVerifier.
                create(result)
                .verifyComplete();


//        verify(entitySyncWebClient, times(1)).makeRequest(issuerCaptor.capture(), entitySyncRequestCaptor.capture());
//        verifyNoMoreInteractions(entitySyncWebClient);
//
//        Mono<String> issuerCaptured = issuerCaptor.getValue();
//
//        StepVerifier
//                .create(issuerCaptured)
//                .expectNext(issuer)
//                .verifyComplete();
//
//        Mono<EntitySyncRequest> entitySyncRequestCaptured = entitySyncRequestCaptor.getValue();
//
//        StepVerifier
//                .create(entitySyncRequestCaptured)
//                .expectNext(entitySyncRequest)
//                .verifyComplete();


    }
}