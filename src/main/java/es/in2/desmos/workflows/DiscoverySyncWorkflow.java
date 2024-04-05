package es.in2.desmos.workflows;

import es.in2.desmos.domain.models.ProductOffering;

import java.util.List;

public interface DiscoverySyncWorkflow {
    List<ProductOffering> discoverySync(String processId, String issuer, List<ProductOffering> productOfferings);
}
