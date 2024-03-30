//package es.in2.desmos.z.services.impl;
//
//import es.in2.desmos.z.domain.model.DLTAdapterSubscription;
//import es.in2.desmos.z.services.DLTAdapterSubscriptionService;
//import es.in2.desmos.services.blockchain.adapter.BlockchainAdapterService;
//import es.in2.desmos.services.blockchain.adapter.factory.BlockchainAdapterFactory;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//public class DLTAdapterSubscriptionServiceImpl implements DLTAdapterSubscriptionService {
//
//    private final BlockchainAdapterService evmAdapter;
//
//    public DLTAdapterSubscriptionServiceImpl(BlockchainAdapterFactory blockchainAdapterFactory) {
//        this.evmAdapter = blockchainAdapterFactory.getEVMAdapter();
//    }
//
//    @Override
//    public Mono<Void> createSubscription(String processId, DLTAdapterSubscription dltAdapterSubscription) {
//        return evmAdapter.createSubscription(processId, dltAdapterSubscription);
//    }
//
//}
