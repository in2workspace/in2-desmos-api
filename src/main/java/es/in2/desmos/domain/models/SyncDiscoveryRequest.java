package es.in2.desmos.domain.models;

import java.util.List;

public record SyncDiscoveryRequest(String issuer, List<ProductOffering> externalEntityIds) {
}
