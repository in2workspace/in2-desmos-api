package es.in2.desmos.domain.events;

import es.in2.desmos.domain.models.DataNegotiationEvent;
import es.in2.desmos.domain.models.EntitiesCreatorEvent;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.objectmothers.EntityMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataNegotiationEventPublisherTests {
    @InjectMocks
    DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Test
    void itShouldPublicateEvent(){

        Mono<String> issuer = Mono.just("https://example.org");
        Mono<List<Entity>> externalEntityIds = Mono.just(EntityMother.list1And2());
        Mono<List<Entity>> internalEntityIds = Mono.just(EntityMother.list3And4());

        DataNegotiationEvent dataNegotiationEvent = new DataNegotiationEvent(issuer, externalEntityIds, internalEntityIds);
        dataNegotiationEventPublisher.publishEvent(dataNegotiationEvent);

        verify(applicationEventPublisher, times(1)).publishEvent(any(EntitiesCreatorEvent.class));
        verifyNoMoreInteractions(applicationEventPublisher);
    }
}
