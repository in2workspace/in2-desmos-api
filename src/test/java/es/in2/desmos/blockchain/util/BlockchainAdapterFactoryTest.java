package es.in2.desmos.blockchain.util;

import es.in2.desmos.blockchain.adapter.DigitelBlockchainAdapter;
import es.in2.desmos.blockchain.config.properties.BlockchainAdapterProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BlockchainAdapterFactoryTest {

    @Mock
    BlockchainAdapterProperties blockchainAdapterProperties;

    @Mock
    DigitelBlockchainAdapter digitelBlockchainAdapter;

    @InjectMocks
    BlockchainAdapterFactory blockchainAdapterFactory;

    @Test
    public void getEVMAdapterWithAnError() {
        when(blockchainAdapterProperties.provider()).thenReturn("invalid");
        Assertions.assertThrows(IllegalArgumentException.class, () -> blockchainAdapterFactory.getEVMAdapter());
    }
}