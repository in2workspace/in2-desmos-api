package es.in2.desmos.domain.models;

import java.util.List;

public record EntitySyncRequest(List<MVEntity4DataNegotiation> entities) {
}