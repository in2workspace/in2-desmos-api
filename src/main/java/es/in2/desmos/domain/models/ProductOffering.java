package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record ProductOffering(
        @JsonProperty("id") @NotNull String id,
        @JsonProperty("version") @NotNull String version,
        @JsonProperty("last_update") @NotNull String lastUpdate) {
}
