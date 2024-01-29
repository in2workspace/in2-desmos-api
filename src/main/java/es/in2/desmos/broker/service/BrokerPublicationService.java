package es.in2.desmos.broker.service;

import reactor.core.publisher.Mono;

public interface BrokerPublicationService {

    Mono<Void> postEntity(String processId, String requestBody);

    Mono<String> getEntityById(String processId, String entityId);

    Mono<Void> updateEntity(String processId, String requestBody);

    Mono<Void> deleteEntityById(String processId, String entityId);

}
