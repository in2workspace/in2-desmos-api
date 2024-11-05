package es.in2.desmos.domain.models;

import java.util.List;

public record DataNegotiationResult(
        String issuer,
        List<MVEntity4DataNegotiation> newEntitiesToSync,
        List<MVEntity4DataNegotiation> existingEntitiesToSync) {
}
