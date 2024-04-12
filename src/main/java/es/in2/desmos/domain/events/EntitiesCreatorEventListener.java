package es.in2.desmos.domain.events;

import es.in2.desmos.domain.models.EntitiesCreatorEvent;
import es.in2.desmos.workflows.jobs.DataNegotiationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntitiesCreatorEventListener {
    private final DataNegotiationJob dataNegotiationJob;

    @EventListener
    public Mono<Void> onApplicationEvent(EntitiesCreatorEvent event) {
        log.info("Received entities creator event.");
        return dataNegotiationJob.negotiateDataSync(event.issuer(), event.externalEntityIds(), event.internalEntityIds());
    }
}
