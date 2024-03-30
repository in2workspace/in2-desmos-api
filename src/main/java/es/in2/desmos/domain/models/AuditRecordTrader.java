package es.in2.desmos.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditRecordTrader {

    PRODUCER("producer"),
    CONSUMER("consumer");

    private final String description;

}
