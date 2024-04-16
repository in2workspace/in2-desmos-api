package es.in2.desmos.workflows.jobs;

import es.in2.desmos.domain.models.DataNegotiationEvent;
import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import es.in2.desmos.workflows.jobs.impl.DataNegotiationJobImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataNegotiationJobTests {
    @InjectMocks
    private DataNegotiationJobImpl dataNegotiationJob;

    @Mock
    private DataTransferJob dataTransferJob;

    @Captor
    private ArgumentCaptor<Mono<DataNegotiationResult>> dataNegotiationResultCaptor;

    @Test
    void itShouldSyncDataWithNewEntitiesToAdd() {
        String issuer = "http://example.org";
        Mono<String> issuerMono = Mono.just(issuer);

        List<MVEntity4DataNegotiation> externalEntityIds = MVEntity4DataNegotiationMother.fullList();
        Mono<List<MVEntity4DataNegotiation>> externalEntityIdsMono = Mono.just(externalEntityIds);

        Mono<List<MVEntity4DataNegotiation>> localEntityIdsMono = Mono.just(MVEntity4DataNegotiationMother.list3And4());

        DataNegotiationEvent dataNegotiationEvent = new DataNegotiationEvent(issuerMono, externalEntityIdsMono, localEntityIdsMono);

        List<MVEntity4DataNegotiation> expectedNewEntitiesToSync = MVEntity4DataNegotiationMother.list1And2();

        List<MVEntity4DataNegotiation> expectedExistingEntitiesToSync = new ArrayList<>();

        DataNegotiationResult expectedDataNegotiationResult = new DataNegotiationResult(issuer, expectedNewEntitiesToSync, expectedExistingEntitiesToSync);

        when(dataTransferJob.syncData(any())).thenReturn(Mono.empty());

        var result = dataNegotiationJob.negotiateDataSync(dataNegotiationEvent);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(dataTransferJob, times(1)).syncData(dataNegotiationResultCaptor.capture());
        verifyNoMoreInteractions(dataTransferJob);

        Mono<DataNegotiationResult> dataNegotiationResultCaptured = dataNegotiationResultCaptor.getValue();

        StepVerifier
                .create(dataNegotiationResultCaptured)
                .expectNext(expectedDataNegotiationResult)
                .verifyComplete();
    }

    @Test
    void itShouldSyncDataWithExistingEntitiesToAddWhenExternalVersionIsAfter() {
        String issuer = "http://example.org";
        Mono<String> issuerMono = Mono.just(issuer);

        List<MVEntity4DataNegotiation> externalEntityIds = MVEntity4DataNegotiationMother.list2And3();
        Mono<List<MVEntity4DataNegotiation>> externalEntityIdsMono = Mono.just(externalEntityIds);

        Mono<List<MVEntity4DataNegotiation>> localEntityIdsMono = Mono.just(List.of(MVEntity4DataNegotiationMother.sample2VersionOld()));

        DataNegotiationEvent dataNegotiationEvent = new DataNegotiationEvent(issuerMono, externalEntityIdsMono, localEntityIdsMono);

        List<MVEntity4DataNegotiation> expectedNewEntitiesToSync = List.of(MVEntity4DataNegotiationMother.sample3());

        List<MVEntity4DataNegotiation> expectedExistingEntitiesToSync = List.of(MVEntity4DataNegotiationMother.sample2());

        DataNegotiationResult expectedDataNegotiationResult = new DataNegotiationResult(issuer, expectedNewEntitiesToSync, expectedExistingEntitiesToSync);

        when(dataTransferJob.syncData(any())).thenReturn(Mono.empty());

        var result = dataNegotiationJob.negotiateDataSync(dataNegotiationEvent);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(dataTransferJob, times(1)).syncData(dataNegotiationResultCaptor.capture());
        verifyNoMoreInteractions(dataTransferJob);

        Mono<DataNegotiationResult> dataNegotiationResultCaptured = dataNegotiationResultCaptor.getValue();

        StepVerifier
                .create(dataNegotiationResultCaptured)
                .expectNext(expectedDataNegotiationResult)
                .verifyComplete();
    }

    @Test
    void itShouldSyncDataWithExistingEntitiesToAddWhenVersionIsEqualAndExternalLastUpdateIsAfter() {
        String issuer = "http://example.org";
        Mono<String> issuerMono = Mono.just(issuer);

        List<MVEntity4DataNegotiation> externalEntityIds = MVEntity4DataNegotiationMother.list2And3();
        Mono<List<MVEntity4DataNegotiation>> externalEntityIdsMono = Mono.just(externalEntityIds);

        Mono<List<MVEntity4DataNegotiation>> localEntityIdsMono = Mono.just(List.of(MVEntity4DataNegotiationMother.sample3TimestampOld()));

        DataNegotiationEvent dataNegotiationEvent = new DataNegotiationEvent(issuerMono, externalEntityIdsMono, localEntityIdsMono);

        List<MVEntity4DataNegotiation> expectedNewEntitiesToSync = List.of(MVEntity4DataNegotiationMother.sample2());

        List<MVEntity4DataNegotiation> expectedExistingEntitiesToSync = List.of(MVEntity4DataNegotiationMother.sample3());

        DataNegotiationResult expectedDataNegotiationResult = new DataNegotiationResult(issuer, expectedNewEntitiesToSync, expectedExistingEntitiesToSync);

        when(dataTransferJob.syncData(any())).thenReturn(Mono.empty());

        var result = dataNegotiationJob.negotiateDataSync(dataNegotiationEvent);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(dataTransferJob, times(1)).syncData(dataNegotiationResultCaptor.capture());
        verifyNoMoreInteractions(dataTransferJob);

        Mono<DataNegotiationResult> dataNegotiationResultCaptured = dataNegotiationResultCaptor.getValue();

        StepVerifier
                .create(dataNegotiationResultCaptured)
                .expectNext(expectedDataNegotiationResult)
                .verifyComplete();
    }

    @Test
    void itShouldNotSyncWhenVersionIsNewer() {
        String issuer = "http://example.org";
        Mono<String> issuerMono = Mono.just(issuer);

        List<MVEntity4DataNegotiation> externalEntityIds = List.of(MVEntity4DataNegotiationMother.sample2VersionOld());
        Mono<List<MVEntity4DataNegotiation>> externalEntityIdsMono = Mono.just(externalEntityIds);

        Mono<List<MVEntity4DataNegotiation>> localEntityIdsMono = Mono.just(List.of(MVEntity4DataNegotiationMother.sample2()));

        DataNegotiationEvent dataNegotiationEvent = new DataNegotiationEvent(issuerMono, externalEntityIdsMono, localEntityIdsMono);

        List<MVEntity4DataNegotiation> expectedNewEntitiesToSync = new ArrayList<>();

        List<MVEntity4DataNegotiation> expectedExistingEntitiesToSync = new ArrayList<>();

        DataNegotiationResult expectedDataNegotiationResult = new DataNegotiationResult(issuer, expectedNewEntitiesToSync, expectedExistingEntitiesToSync);

        when(dataTransferJob.syncData(any())).thenReturn(Mono.empty());

        var result = dataNegotiationJob.negotiateDataSync(dataNegotiationEvent);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(dataTransferJob, times(1)).syncData(dataNegotiationResultCaptor.capture());
        verifyNoMoreInteractions(dataTransferJob);

        Mono<DataNegotiationResult> dataNegotiationResultCaptured = dataNegotiationResultCaptor.getValue();

        StepVerifier
                .create(dataNegotiationResultCaptured)
                .expectNext(expectedDataNegotiationResult)
                .verifyComplete();
    }
}