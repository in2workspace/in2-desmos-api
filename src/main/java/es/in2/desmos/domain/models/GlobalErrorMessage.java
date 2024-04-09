package es.in2.desmos.domain.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GlobalErrorMessage(
        @NotBlank String title,
        @NotBlank String message,
        @NotBlank String path) {
}