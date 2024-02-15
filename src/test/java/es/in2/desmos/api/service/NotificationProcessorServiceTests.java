package es.in2.desmos.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import es.in2.desmos.api.exception.BrokerNotificationParserException;
import es.in2.desmos.api.model.*;
import es.in2.desmos.api.service.impl.NotificationProcessorServiceImpl;
import es.in2.desmos.api.util.ApplicationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationProcessorServiceTests {

    private final Transaction transaction = Transaction.builder()
            .id(UUID.randomUUID())
            .transactionId("testProcessId")
            .createdAt(Timestamp.from(Instant.now()))
            .entityId("testId")
            .entityType("testType")
            .entityHash("testHash")
            .status(TransactionStatus.PUBLISHED)
            .trader(TransactionTrader.CONSUMER)
            .hash("testHash")
            .dataLocation("testLocation")
            .build();
    @Mock
    ObjectWriter objectWriter;
    private BlockchainNotification blockchainNotification = BlockchainNotification.builder()
            .dataLocation("testLocation")
            .build();
    private BrokerNotification brokerNotification = BrokerNotification.builder()
            .data(List.of(Map.of("id", "testId")))
            .build();
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private TransactionService transactionService;
    @InjectMocks
    private NotificationProcessorServiceImpl notificationProcessorService;

    @Test
    void processBrokerNotification_ValidData() throws JsonProcessingException {
        // Arrange
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "testId");
        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString()))
                .thenReturn(Mono.just(transaction));
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(dataMap)).thenReturn("{\"id\":\"testId\"}");
        // Act & Assert
        StepVerifier.create(notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification))
                .expectNextMatches(returnedDataMap -> returnedDataMap.equals(dataMap))
                .verifyComplete();
    }

    @Test
    void processBrokerNotification_InvalidData() {
        // Arrange invalid brokerNotification
        brokerNotification = mock(BrokerNotification.class);
        when(brokerNotification.data()).thenReturn(Collections.emptyList());
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification);
        });
    }

    @Test
    void testNullNotificationData() {
        // Arrange
        List<Map<String, Object>> mockList = mock(List.class);
        when(mockList.get(0)).thenReturn(null);
        BrokerNotification brokerNotification = mock(BrokerNotification.class);
        when(brokerNotification.data()).thenReturn(mockList);
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification);
        });
    }


    @Test
    void testBrokerFromExternalSource() throws JsonProcessingException {
        // Arrange
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "testId");
        Transaction transactionFound = mock(Transaction.class);
        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString()))
                .thenReturn(Mono.just(transaction));
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(dataMap)).thenReturn("{\"id\":\"testId\"}");
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> ApplicationUtils.calculateSHA256Hash(anyString()))
                    .thenReturn("testHash");
            // Act & Assert
            StepVerifier.create(notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification))
                    .expectNext(dataMap)
                    .verifyComplete();
        }
    }

    @Test
    void testTransactionError() throws JsonProcessingException {
        // Arrange
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "testId");
        Transaction transactionFound = mock(Transaction.class);
        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString()))
                .thenReturn(Mono.just(transaction));
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(dataMap)).thenThrow(new JsonProcessingException("") {
        });
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> ApplicationUtils.calculateSHA256Hash(anyString()))
                    .thenReturn("testHash");
            // Act & Assert
            StepVerifier.create(notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification))
                    .verifyError(BrokerNotificationParserException.class);
        }
    }

    @Test
    void processBlockchainNotification_ValidNotification() {
        // Arrange
        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
        // Act & Assert
        StepVerifier.create(notificationProcessorService
                        .processBlockchainNotification("testProcessId", blockchainNotification))
                .verifyComplete();
    }

    @Test
    void processBlockchainNotification_InvalidNotification() {
        // Arrange
        blockchainNotification = mock(BlockchainNotification.class);
        when(blockchainNotification.dataLocation()).thenReturn("");
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            notificationProcessorService.processBlockchainNotification("testProcessId", blockchainNotification);
        });
    }

}