package es.in2.desmos.api.model;

import lombok.Builder;

@Builder
public record GlobalErrorMessage(String title, String message, String path) {
}
