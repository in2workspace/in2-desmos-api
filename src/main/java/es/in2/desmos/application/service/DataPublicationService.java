package es.in2.desmos.application.service;

import reactor.core.publisher.Flux;

public interface DataPublicationService {
    Flux<Void> startPublishingDataToDLT();
}
