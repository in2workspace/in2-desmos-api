package es.in2.desmos.domain.services.sync;

import reactor.core.publisher.Mono;

public interface DataSyncService {

    Mono<Void> synchronizeData(String processId);

}
