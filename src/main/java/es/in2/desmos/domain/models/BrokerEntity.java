package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BrokerEntity(
        @NotBlank
        @JsonProperty("id") String id,

        @NotBlank
        @JsonProperty("type") String type,

        @NotNull
        @JsonProperty("lastUpdate") BrokerAttribute lastUpdate,

        @NotNull
        @JsonProperty("version") BrokerAttribute version,

        @NotNull
        @JsonProperty("@context") List<@NotBlank String> context
) {
}
