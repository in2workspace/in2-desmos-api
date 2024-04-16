package es.in2.desmos.domain.services.api;

import es.in2.desmos.domain.models.BlockchainNotification;
import reactor.core.publisher.Mono;

public interface BrokerEntityRetrievalService {
    Mono<String> retrieveEntityFromExternalBroker(String processId, BlockchainNotification blockchainNotification);
}
