package es.in2.desmos.workflows.jobs.impl;

import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.workflows.jobs.DataTransferJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataTransferJobImpl implements DataTransferJob {
    @Override
    public Mono<Void> syncData(Mono<DataNegotiationResult> dataNegotiationResult) {
        return Mono.empty();
    }
}
