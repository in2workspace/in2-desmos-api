package es.in2.desmos.api.service;//package es.in2.connector.api.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import es.in2.connector.api.model.BlockchainNotification;
//import es.in2.connector.api.model.BrokerNotification;
//import es.in2.connector.api.model.Transaction;
//import es.in2.connector.api.service.impl.NotificationProcessorServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class NotificationProcessorServiceTests {
//
//    // Test data
//    private final BrokerNotification brokerNotification = BrokerNotification.builder()
//            .data(List.of(Map.of("id", "testId")))
//            .build();
//    private final BlockchainNotification blockchainNotification = BlockchainNotification.builder()
//            .dataLocation("testLocation")
//            .build();
//
//    private final Transaction transaction = Transaction.builder()
//            .id(UUID.randomUUID())
//            .entityId("testId")
//            .dataLocation("testLocation")
//            .build();
//
//    @Mock
//    private ObjectMapper objectMapper;
//    @Mock
//    private TransactionService transactionService;
//    @InjectMocks
//    private NotificationProcessorServiceImpl notificationProcessorService;
//
//    @Test
//    void processBrokerNotification_ValidData() {
//        // Arrange
//        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("id", "testId");
//        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(any(), any()))
//                .thenReturn(Mono.just(transaction));
//        // Act & Assert
//        StepVerifier.create(notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification))
//                .expectNextMatches(returnedDataMap -> returnedDataMap.equals(dataMap))
//                .verifyComplete();
//    }
//
//    @Test
//    void processBrokerNotification_InvalidData() {
//        // Arrange invalid brokerNotification
//        // Act & Assert
//        StepVerifier.create(notificationProcessorService
//                        .processBrokerNotification("testProcessId", brokerNotification))
//                .expectError(IllegalArgumentException.class)
//                .verify();
//    }
//
//    @Test
//    void processBlockchainNotification_ValidNotification() {
//        // Mock
//        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
//        // Act & Assert
//        StepVerifier.create(notificationProcessorService
//                        .processBlockchainNotification("testProcessId", blockchainNotification))
//                .verifyComplete();
//    }
//
//    @Test
//    void processBlockchainNotification_InvalidNotification() {
//        // Mock
//        when(notificationProcessorService
//                .processBlockchainNotification("testProcessId", blockchainNotification))
//                .thenThrow(new IllegalArgumentException("Invalid Blockchain Notification"));
//        // Act & Assert
//        StepVerifier.create(notificationProcessorService
//                        .processBlockchainNotification("testProcessId", blockchainNotification))
//                .expectError(IllegalArgumentException.class)
//                .verify();
//    }
//
//}
