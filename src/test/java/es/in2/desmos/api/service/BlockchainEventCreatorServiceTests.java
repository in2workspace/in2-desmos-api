package es.in2.desmos.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.api.config.ApplicationConfig;
import es.in2.desmos.api.exception.HashLinkException;
import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.service.impl.BlockchainEventCreatorServiceImpl;
import es.in2.desmos.api.util.ApplicationUtils;
import es.in2.desmos.broker.config.properties.BrokerPathProperties;
import es.in2.desmos.broker.config.properties.BrokerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainEventCreatorServiceTests {

    @Mock
    private TransactionService transactionService;
    @Mock
    private ApplicationConfig applicationConfig;
    @Mock
    private BrokerProperties brokerProperties;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BlockchainEventCreatorServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BrokerPathProperties brokerPathProperties = new BrokerPathProperties("/v2", "/entities", "/subscriptions");
        BrokerProperties brokerProperties = new BrokerProperties("scorpio", "http://localhost:1026",
                "http://localhost:1026", new BrokerPathProperties("/entities", "/subscriptions", "/v2"));
        service = new BlockchainEventCreatorServiceImpl(brokerProperties, transactionService, applicationConfig, objectMapper);
    }

    @Test
    void createBlockchainEvent_Success() throws JsonProcessingException, NoSuchAlgorithmException {
        // Arrange
        String processId = "testProcessId";
        Map<String, Object> dataMap = Collections.singletonMap("id", "sampleId");
        when(applicationConfig.organizationIdHash()).thenReturn("orgHash");
        when(transactionService.saveTransaction(anyString(), any(Transaction.class))).thenReturn(Mono.empty());
        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        // Act
        Mono<BlockchainEvent> resultMono = service.createBlockchainEvent(processId, dataMap);
        // Assert
        BlockchainEvent result = resultMono.block(); // Blocks until the Mono is completed
        assert result != null;
        assert result.entityId()
                .equals("sampleId");
        // Verify that saveTransaction was called exactly once with any Transaction object as an argument
        verify(transactionService, times(1)).saveTransaction(anyString(), any(Transaction.class));
    }

    @Test
    void createBlockchainEvent_WithHashLinkException() throws JsonProcessingException {
        // Arrange
        String processId = "testProcessId";
        Map<String, Object> dataMap = Collections.singletonMap("id", "sampleId");
        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        // Simulate a condition to throw HashLinkException
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> ApplicationUtils.calculateSHA256Hash(anyString()))
                    .thenThrow(new NoSuchAlgorithmException());
            // Act & Assert
            assertThrows(HashLinkException.class, () -> service.createBlockchainEvent(processId, dataMap));
        }
    }
}