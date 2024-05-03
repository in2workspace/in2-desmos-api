package es.in2.desmos.domain.services.sync.jobs;

import es.in2.desmos.domain.models.DataNegotiationEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;

public interface DataNegotiationJob {

    Mono<Void> negotiateDataSync();

    @EventListener
    Mono<Void> negotiateDataSync(DataNegotiationEvent dataNegotiationEvent);
}
