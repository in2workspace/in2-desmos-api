package es.in2.desmos.application.workflows;

import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DataSyncWorkflow {
    Flux<Void> startDataSyncWorkflow(String processId);
}
