package es.in2.desmos.infrastructure.blockchain.util;

import es.in2.desmos.infrastructure.blockchain.adapter.DigitelDLTAdapter;
import es.in2.desmos.infrastructure.configs.properties.DLTAdapterProperties;
import es.in2.desmos.infrastructure.blockchain.service.GenericDLTAdapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DLTAdapterFactory {

    private final DLTAdapterProperties dltAdapterProperties;
    private final DigitelDLTAdapter digitelDLTAdapter;

    public GenericDLTAdapterService getEVMAdapter() {
        // NOTE: This is a temporary solution until we have more than one EVM adapter. Then we will need to use a switch.
        if (dltAdapterProperties.provider().equals("digitelts")) {
            return digitelDLTAdapter;
        } else {
            throw new IllegalArgumentException("Invalid IAM provider: " + dltAdapterProperties.provider());
        }
    }

}
