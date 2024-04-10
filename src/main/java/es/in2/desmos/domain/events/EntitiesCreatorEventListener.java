package es.in2.desmos.domain.events;

import es.in2.desmos.domain.services.sync.NewEntitiesCreatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntitiesCreatorEventListener {
    private final NewEntitiesCreatorService newEntitiesCreatorService;

    @EventListener
    public void onApplicationEvent(EntitiesCreatorEvent event) {
        log.info("Received entities creator event.");
        newEntitiesCreatorService.addNewEntities(event.issuer(), event.externalEntityIds(), event.internalEntityIds());
    }
}
