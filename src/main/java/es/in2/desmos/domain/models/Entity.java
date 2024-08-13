package es.in2.desmos.domain.models;

import jakarta.validation.constraints.NotBlank;

public record Entity(@NotBlank String value) {
}
