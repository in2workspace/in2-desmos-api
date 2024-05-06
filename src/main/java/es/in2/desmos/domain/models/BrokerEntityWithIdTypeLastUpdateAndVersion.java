package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record BrokerEntityWithIdTypeLastUpdateAndVersion(@JsonProperty("id") @NotBlank String id,
                                                         @JsonProperty("type") @NotBlank String type,
                                                         @JsonProperty("version") @NotBlank String version,
                                                         @JsonProperty("lastUpdate") @NotBlank String lastUpdate) {
}
