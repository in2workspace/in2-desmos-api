package es.in2.desmos.infrastructure.trustframework.cache;

import es.in2.desmos.domain.models.TrustedAccessNodesList;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class TrustedAccessNodesListCache {
    private final Scheduler scheduler = Schedulers.boundedElastic();

    private final AtomicReference<TrustedAccessNodesList> trustedAccessNodesListRef = new AtomicReference<>();

    public void save(Mono<TrustedAccessNodesList> trustedAccessNodesList) {
        trustedAccessNodesList
                .publishOn(scheduler) // Asegura que la operación se ejecute en un hilo adecuado
                .subscribe(trustedAccessNodesListRef::set); // Establece el nuevo valor en AtomicReference
    }

    public Mono<TrustedAccessNodesList> find() {
        TrustedAccessNodesList currentValue = trustedAccessNodesListRef.get();
        return Mono.justOrEmpty(currentValue); // Devuelve un Mono vacío si no hay valor
    }
}