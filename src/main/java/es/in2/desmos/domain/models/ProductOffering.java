package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ProductOffering(
        @JsonProperty("id") @NotBlank String id,
        @JsonProperty("version") @NotBlank String version,
        @JsonProperty("last_update") @NotBlank String lastUpdate) {
}
