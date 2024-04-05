package es.in2.desmos.domain.models;

import java.util.List;

public record DiscoverySyncResponse(String issuer, List<ProductOffering> localEntitiesIds) {
}
