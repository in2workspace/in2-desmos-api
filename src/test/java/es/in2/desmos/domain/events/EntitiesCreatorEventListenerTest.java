package es.in2.desmos.domain.events;

import es.in2.desmos.domain.models.EntitiesCreatorEvent;
import es.in2.desmos.workflows.jobs.DataNegotiationJob;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntitiesCreatorEventListenerTest {
    @InjectMocks
    private EntitiesCreatorEventListener entitiesCreatorEventListener;

    @Mock
    private DataNegotiationJob dataNegotiationJob;

    @Test
    void itShouldCallNewEntitiesService() {
        Mono<String> issuer = Mono.just("https://example.org");
        Mono<List<String>> externalEntityIds = createExternalEntityIdList();
        Mono<List<String>> internalEntityIds = createInternalEntityIds();
        EntitiesCreatorEvent entitiesCreatorEvent = new EntitiesCreatorEvent(issuer, externalEntityIds, internalEntityIds);
        entitiesCreatorEventListener.onApplicationEvent(entitiesCreatorEvent);

        verify(dataNegotiationJob, times(1)).negotiateDataSync(any(), any(), any());
        verifyNoMoreInteractions(dataNegotiationJob);

    }

    private static @NotNull Mono<List<String>> createInternalEntityIds() {
        List<String> internalEntitiesIds = new ArrayList<>();
        internalEntitiesIds.add(EntityMother.sample3().id());
        internalEntitiesIds.add(EntityMother.sample4().id());
        return Mono.just(internalEntitiesIds);
    }

    private static @NotNull Mono<List<String>> createExternalEntityIdList() {
        List<String> externalEntitiesIdList = new ArrayList<>();
        externalEntitiesIdList.add(EntityMother.sample1().id());
        externalEntitiesIdList.add(EntityMother.sample2().id());
        return Mono.just(externalEntitiesIdList);
    }
}
