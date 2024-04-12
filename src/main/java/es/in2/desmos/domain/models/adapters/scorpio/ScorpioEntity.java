package es.in2.desmos.domain.models.adapters.scorpio;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ScorpioEntity(
        @NotBlank
        @JsonProperty("id") String id,

        @NotBlank
        @JsonProperty("type") String type,

        @NotNull
        @JsonProperty("lastUpdate") ScorpioAttribute lastUpdate,

        @NotNull
        @JsonProperty("version") ScorpioAttribute version,

        @NotNull
        @JsonProperty("hash") ScorpioAttribute hash,

        @NotNull
        @JsonProperty("hashlink") ScorpioAttribute hashlink,

        @NotNull
        @JsonProperty("@context") List<@NotBlank String> context
) {
}
