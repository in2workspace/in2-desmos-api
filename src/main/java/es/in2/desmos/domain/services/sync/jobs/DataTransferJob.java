package es.in2.desmos.domain.services.sync.jobs;

import es.in2.desmos.domain.models.DataNegotiationResult;
import reactor.core.publisher.Mono;

public interface DataTransferJob {
    Mono<Void> syncData(String processId, Mono<DataNegotiationResult> dataNegotiationResult);
}
