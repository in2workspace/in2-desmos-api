package es.in2.desmos.domain.models;

import reactor.core.publisher.Mono;

import java.util.List;

public record EntitiesCreatorEvent(Mono<String> issuer, Mono<List<String>> externalEntityIds,
                                   Mono<List<String>> internalEntityIds) {
}
