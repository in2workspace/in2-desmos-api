package es.in2.desmos.api.service;

import es.in2.desmos.api.model.BlockchainNotification;
import reactor.core.publisher.Mono;

public interface BrokerEntityRetrievalService {
    Mono<String> retrieveEntityFromSourceBroker(String processId, BlockchainNotification blockchainNotification);
}
