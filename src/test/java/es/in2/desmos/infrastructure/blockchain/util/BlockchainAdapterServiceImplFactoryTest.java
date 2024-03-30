package es.in2.desmos.infrastructure.blockchain.util;
import es.in2.desmos.configs.properties.DLTAdapterProperties;
import es.in2.desmos.services.blockchain.adapter.factory.BlockchainAdapterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockchainAdapterServiceImplFactoryTest {

    @Mock
    DLTAdapterProperties DLTAdapterProperties;

    @InjectMocks
    BlockchainAdapterFactory BlockchainAdapterFactory;

    @Test
    void getEVMAdapterWithAnError() {
        when(DLTAdapterProperties.provider()).thenReturn("invalid");
        Assertions.assertThrows(IllegalArgumentException.class, () -> BlockchainAdapterFactory.getBlockchainAdapter());
    }
}
