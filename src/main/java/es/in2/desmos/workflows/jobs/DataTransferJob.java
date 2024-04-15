package es.in2.desmos.workflows.jobs;

import es.in2.desmos.domain.models.DataNegotiationResult;
import reactor.core.publisher.Mono;

public interface DataTransferJob {
    Mono<Void> syncData(Mono<DataNegotiationResult> dataNegotiationResult);
}
