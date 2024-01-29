package es.in2.desmos.broker.util;

import es.in2.desmos.broker.adapter.OrionLdAdapter;
import es.in2.desmos.broker.adapter.ScorpioAdapter;
import es.in2.desmos.broker.config.properties.BrokerProperties;
import es.in2.desmos.broker.service.GenericBrokerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerFactoryTest {

    @Mock
    private BrokerProperties brokerProperties;
    @Mock
    private ScorpioAdapter scorpioAdapter;
    @Mock
    private OrionLdAdapter orionLdAdapter;

    @InjectMocks
    private BrokerFactory brokerFactory;

    @BeforeEach
    void setUp() {
        // Initial setup if needed...
    }

    @Test
    void testGetScorpioAdapter() {
        // Arrange
        when(brokerProperties.provider()).thenReturn("scorpio");
        // Act
        GenericBrokerService result = brokerFactory.getBrokerAdapter();
        // Assert
        assertSame(scorpioAdapter, result);
    }

    @Test
    void testGetOrionLdAdapter() {
        // Arrange
        when(brokerProperties.provider()).thenReturn("orion-ld");
        // Act
        GenericBrokerService result = brokerFactory.getBrokerAdapter();
        // Assert
        assertSame(orionLdAdapter, result);
    }

    @Test
    void testGetBrokerAdapterWithInvalidProvider() {
        // Arrange
        when(brokerProperties.provider()).thenReturn("invalid-provider");
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> brokerFactory.getBrokerAdapter());
        assertTrue(exception.getMessage().contains("Invalid IAM provider: invalid-provider"));
    }

    // Add more tests if needed...
}

