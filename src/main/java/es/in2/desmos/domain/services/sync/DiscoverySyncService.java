package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.ProductOffering;

import java.util.List;

public interface DiscoverySyncService {
    List<ProductOffering> discoverySync(String processId, String issuer, List<ProductOffering> productOfferings);
}
