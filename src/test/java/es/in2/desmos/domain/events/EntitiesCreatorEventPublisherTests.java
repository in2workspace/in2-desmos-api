package es.in2.desmos.domain.events;

import es.in2.desmos.domain.models.EntitiesCreatorEvent;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntitiesCreatorEventPublisherTests {
    @InjectMocks
    EntitiesCreatorEventPublisher entitiesCreatorEventPublisher;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Test
    void itShouldPublicateEvent(){

        Mono<String> issuer = Mono.just("https://example.org");
        Mono<List<String>> externalEntityIds = createExternalEntityIdList();
        Mono<List<String>> internalEntityIds = createInternalEntityIds();

        entitiesCreatorEventPublisher.publishEvent(issuer, externalEntityIds, internalEntityIds);

        verify(applicationEventPublisher, times(1)).publishEvent(any(EntitiesCreatorEvent.class));
        verifyNoMoreInteractions(applicationEventPublisher);

    }

    private static @NotNull Mono<List<String>> createInternalEntityIds() {
        List<String> internalEntitiesIds = new ArrayList<>();
        internalEntitiesIds.add(ProductOfferingMother.sample3().id());
        internalEntitiesIds.add(ProductOfferingMother.sample4().id());
        return Mono.just(internalEntitiesIds);
    }

    private static @NotNull Mono<List<String>> createExternalEntityIdList() {
        List<String> externalEntitiesIdList = new ArrayList<>();
        externalEntitiesIdList.add(ProductOfferingMother.sample1().id());
        externalEntitiesIdList.add(ProductOfferingMother.sample2().id());
        return Mono.just(externalEntitiesIdList);
    }
}
