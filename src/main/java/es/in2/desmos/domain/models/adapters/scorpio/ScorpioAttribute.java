package es.in2.desmos.domain.models.adapters.scorpio;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScorpioAttribute(
        @NotBlank
        @JsonProperty("type") String type,

        @NotNull
        @JsonProperty("value") String value
) {
}
