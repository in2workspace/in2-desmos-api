package es.in2.desmos.blockchain.util;

import es.in2.desmos.blockchain.adapter.DigitelBlockchainAdapter;
import es.in2.desmos.blockchain.config.properties.BlockchainAdapterProperties;
import es.in2.desmos.blockchain.service.GenericBlockchainAdapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlockchainAdapterFactory {

    private final BlockchainAdapterProperties blockchainAdapterProperties;
    private final DigitelBlockchainAdapter digitelBlockchainAdapter;

    public GenericBlockchainAdapterService getEVMAdapter() {
        // NOTE: This is a temporary solution until we have more than one EVM adapter. Then we will need to use a switch.
        if (blockchainAdapterProperties.provider().equals("digitelts")) {
            return digitelBlockchainAdapter;
        } else {
            throw new IllegalArgumentException("Invalid IAM provider: " + blockchainAdapterProperties.provider());
        }
    }

}
