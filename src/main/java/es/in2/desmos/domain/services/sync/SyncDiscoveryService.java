package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.ProductOffering;

import java.util.List;

public interface SyncDiscoveryService {
    List<ProductOffering> syncDiscovery(String processId, String issuer, List<ProductOffering> productOfferings);
}
