package es.in2.desmos.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionTrader {

    PRODUCER("producer"),
    CONSUMER("consumer");

    private final String description;

}
