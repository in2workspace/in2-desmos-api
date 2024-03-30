//package es.in2.desmos.z.services.impl;
//
//import es.in2.desmos.z.domain.model.DLTEvent;
//import es.in2.desmos.z.services.DLTAdapterPublisher;
//import es.in2.desmos.services.blockchain.adapter.BlockchainAdapterService;
//import es.in2.desmos.services.blockchain.adapter.factory.BlockchainAdapterFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Service
//public class DLTAdapterPublisherImpl implements DLTAdapterPublisher {
//
//    private final BlockchainAdapterService evmAdapter;
//
//    public DLTAdapterPublisherImpl(BlockchainAdapterFactory blockchainAdapterFactory) {
//        this.evmAdapter = blockchainAdapterFactory.getEVMAdapter();
//    }
//
//    @Override
//    public Mono<Void> publishBlockchainEvent(String processId, DLTEvent dltEvent) {
//        return evmAdapter.publishEvent(processId, dltEvent);
//    }
//
//    @Override
//    public Flux<String> getEventsFromRange(String processId, long from, long to) {
//        return evmAdapter.getEventsFromRange(processId, from, to);
//    }
//
//
//
//
//}
