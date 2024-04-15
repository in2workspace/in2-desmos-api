package es.in2.desmos.workflows.jobs;

import es.in2.desmos.domain.models.DataNegotiationEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;

public interface DataNegotiationJob {
    @EventListener
    Mono<Void> negotiateDataSync(DataNegotiationEvent dataNegotiationEvent);
}
