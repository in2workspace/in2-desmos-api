package es.in2.desmos.workflows.jobs;

import reactor.core.publisher.Mono;

import java.util.List;

public interface DataNegotiationJob {
    Mono<Void> negotiateDataSync(Mono<String> issuer, Mono<List<String>> externalEntityIds, Mono<List<String>> internalEntityIds);
}
