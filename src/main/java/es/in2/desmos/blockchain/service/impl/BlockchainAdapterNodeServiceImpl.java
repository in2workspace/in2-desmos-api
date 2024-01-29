package es.in2.desmos.blockchain.service.impl;

import es.in2.desmos.blockchain.model.BlockchainNode;
import es.in2.desmos.blockchain.service.BlockchainAdapterNodeService;
import es.in2.desmos.blockchain.service.GenericBlockchainAdapterService;
import es.in2.desmos.blockchain.util.BlockchainAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BlockchainAdapterNodeServiceImpl implements BlockchainAdapterNodeService {

    private final GenericBlockchainAdapterService evmAdapter;

    public BlockchainAdapterNodeServiceImpl(BlockchainAdapterFactory blockchainAdapterFactory) {
        this.evmAdapter = blockchainAdapterFactory.getEVMAdapter();
    }

    @Override
    public Mono<String> createBlockchainNodeConnection(String processId, BlockchainNode blockchainNode) {
        return evmAdapter.setNodeConnection(processId, blockchainNode);
    }

}
