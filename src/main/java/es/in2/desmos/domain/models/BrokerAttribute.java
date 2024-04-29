package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BrokerAttribute(
        @NotBlank
        @JsonProperty("type") String type,

        @NotNull
        @JsonProperty("value") String value
) {
}
