package es.in2.desmos.domain.events;

import es.in2.desmos.domain.models.EntitiesCreatorEvent;
import es.in2.desmos.domain.services.sync.NewEntitiesCreatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntitiesCreatorEventListener {
    private final NewEntitiesCreatorService newEntitiesCreatorService;

    @EventListener
    public Mono<Void> onApplicationEvent(EntitiesCreatorEvent event) {
        log.info("Received entities creator event.");
        return newEntitiesCreatorService.addNewEntities(event.issuer(), event.externalEntityIds(), event.internalEntityIds());
    }
}
