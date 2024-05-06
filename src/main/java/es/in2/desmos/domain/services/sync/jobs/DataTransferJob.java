package es.in2.desmos.domain.services.sync.jobs;

import es.in2.desmos.domain.models.DataNegotiationResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DataTransferJob {
    Mono<Void> syncDataFromList(String processId, Mono<List<DataNegotiationResult>> dataNegotiationResult);

    Mono<Void> syncData(String processId, Mono<DataNegotiationResult> dataNegotiationResult);
}
