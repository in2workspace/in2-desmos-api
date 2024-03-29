package es.in2.desmos.infrastructure.blockchain.service.impl;

import es.in2.desmos.domain.model.DLTEvent;
import es.in2.desmos.infrastructure.blockchain.service.DLTAdapterPublisher;
import es.in2.desmos.infrastructure.blockchain.service.GenericDLTAdapterService;
import es.in2.desmos.infrastructure.blockchain.util.DLTAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DLTAdapterPublisherImpl implements DLTAdapterPublisher {

    private final GenericDLTAdapterService evmAdapter;

    public DLTAdapterPublisherImpl(DLTAdapterFactory dltAdapterFactory) {
        this.evmAdapter = dltAdapterFactory.getEVMAdapter();
    }

    @Override
    public Mono<Void> publishBlockchainEvent(String processId, DLTEvent dltEvent) {
        return evmAdapter.publishEvent(processId, dltEvent);
    }

    @Override
    public Flux<String> getEventsFromRange(String processId, long from, long to) {
        return evmAdapter.getEventsFromRange(processId, from, to);
    }




}
