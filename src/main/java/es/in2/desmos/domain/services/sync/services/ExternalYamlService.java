package es.in2.desmos.domain.services.sync.services;

import reactor.core.publisher.Mono;


public interface ExternalYamlService {

    Mono<Void> getAccessNodeYamlDataFromExternalSource(String processId);
}
