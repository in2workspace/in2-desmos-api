package es.in2.desmos.domain.models;

import reactor.core.publisher.Mono;

import java.util.List;

public record DataNegotiationResult(
        Mono<String> issuer,
        Mono<List<MVEntity4DataNegotiation>> newEntitiesToSync,
        Mono<List<MVEntity4DataNegotiation>> existingEntitiesToSync) {
}
