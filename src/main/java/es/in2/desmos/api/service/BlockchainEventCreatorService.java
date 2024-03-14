package es.in2.desmos.api.service;

import es.in2.desmos.api.model.BlockchainEvent;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BlockchainEventCreatorService {
    Mono<BlockchainEvent> createBlockchainEvent(String processId, Map<String, Object> dataMap);
}
