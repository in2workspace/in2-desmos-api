package es.in2.desmos.api.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface BrokerEntityProcessorService {
    Mono<Map<String, Object>> processBrokerEntity(String processId, String brokerEntityId);
}
