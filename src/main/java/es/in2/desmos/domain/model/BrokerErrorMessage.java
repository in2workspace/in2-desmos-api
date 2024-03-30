package es.in2.desmos.domain.model;

import lombok.Builder;

@Builder
public record BrokerErrorMessage(String type, String title, Detail detail, String errorCode) {
    public record Detail(String message) {}
}
