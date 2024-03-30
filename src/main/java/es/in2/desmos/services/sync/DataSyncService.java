package es.in2.desmos.services.sync;

import reactor.core.publisher.Mono;

public interface DataSyncService {

    Mono<Void> synchronizeData(String processId);

}
