package es.in2.desmos.workflows.jobs;

import es.in2.desmos.domain.services.sync.EntitySyncWebClient;
import es.in2.desmos.workflows.jobs.impl.DataNegotiationJobImpl;
import es.in2.desmos.objectmothers.EntityMother;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DataNegotiationJobTests {
    @InjectMocks
    private DataNegotiationJobImpl dataNegotiationJob;

    @Mock
    private EntitySyncWebClient entitySyncWebClient;

    @Test
    void itShouldCreateNewEntitiesIfNotExists() {
       /* Mono<List<String>> externalEntitiesIdList = Mono.just(createExternalEntitiesIdList());

        Mono<String> issuer = Mono.just("http://example.org");

        Mono<List<String>> internalEntitiesIds = getInternalEntitiesIdsMono();

        Mono<EntitySyncResponse> entitySyncResponse = Mono.just(EntitySyncResponseMother.sample());
        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponse);

        Mono<Void> result = newEntitiesCreatorService.addNewEntities(issuer, externalEntitiesIdList, internalEntitiesIds);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(entitySyncWebClient, times(1)).makeRequest(any(), any());
        verifyNoMoreInteractions(entitySyncWebClient);*/
    }

    private @NotNull Mono<List<String>> getInternalEntitiesIdsMono() {
        List<String> internalEntitiesIds = new ArrayList<>();
        internalEntitiesIds.add(EntityMother.sample3().id());
        internalEntitiesIds.add(EntityMother.sample4().id());
        return Mono.just(internalEntitiesIds);
    }

    private @NotNull List<String> createExternalEntitiesIdList() {
        List<String> externalEntitiesIdList = new ArrayList<>();
        externalEntitiesIdList.add(EntityMother.sample1().id());
        externalEntitiesIdList.add(EntityMother.sample2().id());
        return externalEntitiesIdList;
    }
}