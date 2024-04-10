package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.EntitySyncResponse;
import es.in2.desmos.domain.services.sync.impl.NewEntitiesCreatorServiceImpl;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewEntitiesCreatorServiceTests {
    @InjectMocks
    private NewEntitiesCreatorServiceImpl newEntitiesCreatorService;

    @Mock
    private EntitySyncWebClient entitySyncWebClient;

    @Test
    void itShouldCreateNewEntitiesIfNotExists() {
        Mono<List<String>> externalEntitiesIdList = Mono.just(createExternalEntitiesIdList());

        Mono<String> issuer = Mono.just("http://example.org");

        Mono<List<String>> internalEntitiesIds = getInternalEntitiesIdsMono();

        Mono<EntitySyncResponse> entitySyncResponse = Mono.just(EntitySyncResponseMother.sample());
        when(entitySyncWebClient.makeRequest(any(), any())).thenReturn(entitySyncResponse);

        Mono<Void> result = newEntitiesCreatorService.addNewEntities(issuer, externalEntitiesIdList, internalEntitiesIds);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(entitySyncWebClient, times(1)).makeRequest(any(), any());
        verifyNoMoreInteractions(entitySyncWebClient);
    }

    private static @NotNull Mono<List<String>> getInternalEntitiesIdsMono() {
        List<String> internalEntitiesIds = new ArrayList<>();
        internalEntitiesIds.add(ProductOfferingMother.sample3().id());
        internalEntitiesIds.add(ProductOfferingMother.sample4().id());
        return Mono.just(internalEntitiesIds);
    }

    private static @NotNull List<String> createExternalEntitiesIdList() {
        List<String> externalEntitiesIdList = new ArrayList<>();
        externalEntitiesIdList.add(ProductOfferingMother.sample1().id());
        externalEntitiesIdList.add(ProductOfferingMother.sample2().id());
        return externalEntitiesIdList;
    }
}