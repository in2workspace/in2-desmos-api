package es.in2.desmos.domain.services.blockchain.adapter.factory;

import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.domain.services.blockchain.adapter.impl.BlockchainAdapterServiceImpl;
import es.in2.desmos.infrastructure.configs.properties.DLTAdapterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlockchainAdapterFactory {

    private final DLTAdapterProperties dltAdapterProperties;
    private final BlockchainAdapterServiceImpl blockchainAdapterServiceImpl;

    public BlockchainAdapterService getBlockchainAdapter() {
        if (dltAdapterProperties.provider().equals("digitelts")) {
            return blockchainAdapterServiceImpl;
        } else {
            throw new IllegalArgumentException("Invalid Blockchain Adapter provider: " + dltAdapterProperties.provider());
        }
    }

}
