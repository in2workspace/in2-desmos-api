package es.in2.desmos.domain.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GlobalErrorMessage(
        @NotBlank(message = "title must not be blank")
        String title,

        @NotBlank(message = "message must not be blank")
        String message,

        @NotBlank(message = "path must not be blank")
        String path) {
}