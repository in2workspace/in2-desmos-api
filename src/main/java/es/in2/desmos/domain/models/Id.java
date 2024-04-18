package es.in2.desmos.domain.models;

import jakarta.validation.constraints.NotBlank;

public record Id(@NotBlank String value) {
}
