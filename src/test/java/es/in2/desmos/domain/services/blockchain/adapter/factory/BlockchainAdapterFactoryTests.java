package es.in2.desmos.domain.services.blockchain.adapter.factory;

import es.in2.desmos.domain.services.blockchain.adapter.impl.BlockchainAdapterServiceImpl;
import es.in2.desmos.infrastructure.configs.properties.DLTAdapterProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainAdapterFactoryTests {

    @Mock
    private DLTAdapterProperties dltAdapterProperties;
    @Mock
    private BlockchainAdapterServiceImpl blockchainAdapterServiceImpl;
    @InjectMocks
    private BlockchainAdapterFactory blockchainAdapterFactory;

    @Test
    void getBlockchainAdapter_ValidProvider() {
        // Arrange
        when(dltAdapterProperties.provider()).thenReturn("digitelts");

        // Act & Assert
        assertNotNull(blockchainAdapterFactory.getBlockchainAdapter());
        verify(dltAdapterProperties, times(1)).provider();
    }

    @Test
    void getBlockchainAdapter_InvalidProvider() {
        // Arrange
        when(dltAdapterProperties.provider()).thenReturn("invalid");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, blockchainAdapterFactory::getBlockchainAdapter);
    }
}