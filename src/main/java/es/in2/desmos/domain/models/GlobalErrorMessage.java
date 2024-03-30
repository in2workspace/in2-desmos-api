package es.in2.desmos.domain.models;

import lombok.Builder;

@Builder
public record GlobalErrorMessage(String title, String message, String path) {
}
