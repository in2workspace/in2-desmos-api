package es.in2.desmos.domain.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntitiesCreatorEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(Mono<String> issuer, Mono<List<String>> externalEntityIds,
                             Mono<List<String>> internalEntityIds) {
        log.info("Publishing entities creator event.");
        EntitiesCreatorEvent entitiesCreatorEvent = new EntitiesCreatorEvent(issuer, externalEntityIds, internalEntityIds);
        applicationEventPublisher.publishEvent(entitiesCreatorEvent);
    }
}
