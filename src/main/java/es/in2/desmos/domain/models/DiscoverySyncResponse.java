package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DiscoverySyncResponse(
        @JsonProperty("issuer") @NotNull String issuer,
        @JsonProperty("external_entity_ids") @NotNull List<ProductOffering> localEntitiesIds) {
}
