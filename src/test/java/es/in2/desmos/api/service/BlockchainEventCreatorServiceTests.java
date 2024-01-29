package es.in2.desmos.api.service;//package es.in2.connector.api.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import es.in2.connector.api.configuration.ApplicationConfig;
//import es.in2.connector.api.exception.HashLinkException;
//import es.in2.connector.api.model.BlockchainEvent;
//import es.in2.connector.api.model.Transaction;
//import es.in2.connector.api.service.impl.BlockchainEventCreatorServiceImpl;
//import es.in2.connector.broker.config.properties.BrokerPathProperties;
//import es.in2.connector.broker.config.properties.BrokerProperties;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import reactor.core.publisher.Mono;
//
//import java.util.Collections;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class BlockchainEventCreatorServiceTests {
//
//    @Mock
//    private TransactionService transactionService;
//    @Mock
//    private ApplicationConfig applicationConfig;
//    @Mock
//    private BrokerProperties brokerProperties;
//    @Mock
//    private ObjectMapper objectMapper;
//    @InjectMocks
//    private BlockchainEventCreatorServiceImpl service;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        BrokerPathProperties brokerPathProperties = new BrokerPathProperties("/v2", "/entities");
//        BrokerProperties brokerProperties = new BrokerProperties("scorpio", "http://localhost:1026",
//                "http://localhost:1026", new BrokerPathProperties("/entities", "/subscriptions"));
//        service = new BlockchainEventCreatorServiceImpl(brokerProperties, transactionService, applicationConfig, objectMapper);
//    }
//    @Test
//    void createBlockchainEvent_Success() {
//        // Arrange
//        String processId = "testProcessId";
//        Map<String, Object> dataMap = Collections.singletonMap("id", "sampleId");
//        when(applicationConfig.organizationIdHash()).thenReturn("orgHash");
//        when(transactionService.saveTransaction(anyString(), any(Transaction.class))).thenReturn(Mono.empty());
//        // Act
//        Mono<BlockchainEvent> resultMono = service.createBlockchainEvent(processId, dataMap);
//        // Assert
//        BlockchainEvent result = resultMono.block(); // Blocks until the Mono is completed
//        assert result != null;
//        assert result.entityId().equals("sampleId");
//        // Verify that saveTransaction was called exactly once with any Transaction object as an argument
//        verify(transactionService, times(1)).saveTransaction(anyString(), any(Transaction.class));
//    }
//
//    @Test
//    void createBlockchainEvent_WithHashLinkException() {
//        // Arrange
//        String processId = "testProcessId";
//        Map<String, Object> dataMap = Collections.singletonMap("id", "sampleId");
//        when(applicationConfig.organizationIdHash()).thenReturn("orgHash");
//        when(transactionService.saveTransaction(anyString(), any(Transaction.class))).thenReturn(Mono.empty());
//        // Simulate a condition to throw HashLinkException
//        doThrow(new HashLinkException("Error creating blockchain event"))
//                .when(service.createBlockchainEvent(processId, dataMap));
//        // Act & Assert
//        assertThrows(HashLinkException.class, () -> service.createBlockchainEvent(processId, dataMap).block());
//    }
//
//
//}
