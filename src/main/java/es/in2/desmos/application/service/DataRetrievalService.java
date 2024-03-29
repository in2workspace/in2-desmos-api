package es.in2.desmos.application.service;

import reactor.core.publisher.Flux;

public interface DataRetrievalService {
    Flux<Void> startRetrievingData();
}
