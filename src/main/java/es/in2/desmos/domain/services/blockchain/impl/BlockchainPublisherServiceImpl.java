package es.in2.desmos.domain.services.blockchain.impl;

import es.in2.desmos.domain.models.BlockchainTxPayload;
import es.in2.desmos.domain.services.blockchain.BlockchainPublisherService;
import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.domain.services.blockchain.adapter.factory.BlockchainAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BlockchainPublisherServiceImpl implements BlockchainPublisherService {

    private final BlockchainAdapterService blockchainAdapterService;

    public BlockchainPublisherServiceImpl(BlockchainAdapterFactory blockchainAdapterFactory) {
        this.blockchainAdapterService = blockchainAdapterFactory.getBlockchainAdapter();
    }

    @Override
    public Mono<Void> PublishBlockchainTxPayloadToDltAdapter(String processId, BlockchainTxPayload blockchainTxPayload) {
        return blockchainAdapterService.postTxPayload(processId, blockchainTxPayload);
    }

}