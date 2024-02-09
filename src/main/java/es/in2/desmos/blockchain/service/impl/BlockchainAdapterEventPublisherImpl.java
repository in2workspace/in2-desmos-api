package es.in2.desmos.blockchain.service.impl;

import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.blockchain.service.BlockchainAdapterEventPublisher;
import es.in2.desmos.blockchain.service.GenericBlockchainAdapterService;
import es.in2.desmos.blockchain.util.BlockchainAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BlockchainAdapterEventPublisherImpl implements BlockchainAdapterEventPublisher {

    private final GenericBlockchainAdapterService evmAdapter;

    public BlockchainAdapterEventPublisherImpl(BlockchainAdapterFactory blockchainAdapterFactory) {
        this.evmAdapter = blockchainAdapterFactory.getEVMAdapter();
    }

    @Override
    public Mono<Void> publishBlockchainEvent(String processId, BlockchainEvent blockchainEvent) {
        return evmAdapter.publishEvent(processId, blockchainEvent);
    }

    @Override
    public Flux<String> getEventsFromRange(String processId, long from, long to) {
        return evmAdapter.getEventsFromRange(processId, from, to);
    }




}
