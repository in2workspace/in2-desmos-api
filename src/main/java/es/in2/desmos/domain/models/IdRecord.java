package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record IdRecord(@JsonProperty("id") @NotBlank String id) {
}
