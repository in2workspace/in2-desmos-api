package es.in2.desmos.domain.models;

import java.util.List;

public record DiscoverySyncRequest(String issuer, List<ProductOffering> externalEntityIds) {
}
