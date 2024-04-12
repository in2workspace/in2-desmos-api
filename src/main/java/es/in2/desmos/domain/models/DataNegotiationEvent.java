package es.in2.desmos.domain.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;

public record DataNegotiationEvent(
        @NotNull Mono<@NotEmpty String> issuer,
        @NotNull Mono<@NotNull List<@NotNull Entity>> externalEntityIds,
        @NotNull Mono<@NotNull List<@NotEmpty String>> internalEntityIds) {
}
