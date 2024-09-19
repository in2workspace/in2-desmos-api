package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record BrokerEntityValidFor(@JsonProperty("startDateTime")
                                   @NotBlank String startDateTime
) {}