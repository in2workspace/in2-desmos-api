package es.in2.desmos.domain.services.broker.adapter.factory;

import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.impl.OrionLdAdapter;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.infrastructure.configs.properties.BrokerProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerAdapterFactoryTests {

    @Mock
    private BrokerProperties brokerPathProperties;

    @Mock
    private ScorpioAdapter scorpioAdapter;

    @Mock
    private OrionLdAdapter orionLdAdapter;

    @InjectMocks
    private BrokerAdapterFactory brokerAdapterFactory;

    @Test
    void whenProviderIsScorpio_thenScorpioAdapterIsReturned() {
        // Arrange
        when(brokerPathProperties.provider()).thenReturn("scorpio");

        // Act
        BrokerAdapterService brokerAdapterService = brokerAdapterFactory.getBrokerAdapter();

        // Assert
        assertEquals(scorpioAdapter, brokerAdapterService);
    }


    @Test
    void whenProviderIsOrionLd_thenOrionLdAdapterIsReturned() {
        // Arrange
        when(brokerPathProperties.provider()).thenReturn("orion-ld");

        // Act
        BrokerAdapterService brokerAdapterService = brokerAdapterFactory.getBrokerAdapter();

        // Assert
        assertEquals(orionLdAdapter, brokerAdapterService);
    }

    @Test
    void whenProviderIsInvalid_thenExceptionIsThrown() {
        // Arrange
        when(brokerPathProperties.provider()).thenReturn("invalid");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> brokerAdapterFactory.getBrokerAdapter());
    }
}