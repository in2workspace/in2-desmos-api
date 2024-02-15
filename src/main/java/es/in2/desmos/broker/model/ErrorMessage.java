package es.in2.desmos.broker.model;

import lombok.Builder;

@Builder
public record ErrorMessage(String type, String title, Detail detail, String errorCode) {
    public record Detail(String message) {}
}
