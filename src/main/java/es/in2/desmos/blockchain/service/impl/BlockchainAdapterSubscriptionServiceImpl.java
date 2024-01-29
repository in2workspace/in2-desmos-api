package es.in2.desmos.blockchain.service.impl;

import es.in2.desmos.blockchain.model.BlockchainAdapterSubscription;
import es.in2.desmos.blockchain.service.BlockchainAdapterSubscriptionService;
import es.in2.desmos.blockchain.service.GenericBlockchainAdapterService;
import es.in2.desmos.blockchain.util.BlockchainAdapterFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BlockchainAdapterSubscriptionServiceImpl implements BlockchainAdapterSubscriptionService {

    private final GenericBlockchainAdapterService evmAdapter;

    public BlockchainAdapterSubscriptionServiceImpl(BlockchainAdapterFactory blockchainAdapterFactory) {
        this.evmAdapter = blockchainAdapterFactory.getEVMAdapter();
    }

    @Override
    public Mono<Void> createSubscription(String processId, BlockchainAdapterSubscription blockchainAdapterSubscription) {
        return evmAdapter.createSubscription(processId, blockchainAdapterSubscription);
    }


}
