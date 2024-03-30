//package es.in2.desmos.todo.infrastructure.broker.util;
//
//import es.in2.desmos.services.broker.adapter.impl.OrionLdAdapter;
//import es.in2.desmos.services.broker.adapter.impl.ScorpioAdapter;
//import es.in2.desmos.configs.properties.BrokerProperties;
//import es.in2.desmos.services.broker.adapter.BrokerAdapterService;
//import es.in2.desmos.services.broker.adapter.factory.BrokerAdapterFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class BrokerAdapterFactoryTest {
//
//    @Mock
//    private BrokerProperties brokerProperties;
//    @Mock
//    private ScorpioAdapter scorpioAdapter;
//    @Mock
//    private OrionLdAdapter orionLdAdapter;
//
//    @InjectMocks
//    private BrokerAdapterFactory brokerAdapterFactory;
//
//    @BeforeEach
//    void setUp() {
//        // Initial setup if needed...
//    }
//
//    @Test
//    void testGetScorpioAdapter() {
//        // Arrange
//        when(brokerProperties.provider()).thenReturn("scorpio");
//        // Act
//        BrokerAdapterService result = brokerAdapterFactory.getBrokerAdapter();
//        // Assert
//        assertSame(scorpioAdapter, result);
//    }
//
//    @Test
//    void testGetOrionLdAdapter() {
//        // Arrange
//        when(brokerProperties.provider()).thenReturn("orion-ld");
//        // Act
//        BrokerAdapterService result = brokerAdapterFactory.getBrokerAdapter();
//        // Assert
//        assertSame(orionLdAdapter, result);
//    }
//
//    @Test
//    void testGetBrokerAdapterWithInvalidProvider() {
//        // Arrange
//        when(brokerProperties.provider()).thenReturn("invalid-provider");
//        // Act & Assert
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> brokerAdapterFactory.getBrokerAdapter());
//        assertTrue(exception.getMessage().contains("Invalid IAM provider: invalid-provider"));
//    }
//
//    // Add more tests if needed...
//}
//
