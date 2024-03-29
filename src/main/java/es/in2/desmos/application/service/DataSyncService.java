package es.in2.desmos.application.service;

import reactor.core.publisher.Mono;

public interface DataSyncService {

    Mono<Void> synchronizeData(String processId);

}
