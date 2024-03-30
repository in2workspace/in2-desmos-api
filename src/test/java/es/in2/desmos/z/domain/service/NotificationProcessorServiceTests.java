//package es.in2.desmos.todo.domain.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import es.in2.desmos.domain.models.*;
//import es.in2.desmos.domain.services.QueueService;
//import es.in2.desmos.z.services.TransactionService;
//import es.in2.desmos.z.services.impl.NotificationProcessorServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import reactor.core.publisher.Mono;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class NotificationProcessorServiceTests {
//
//    @Mock
//    private ObjectWriter objectWriter;
//    @Mock
//    private ObjectMapper objectMapper;
//    @Mock
//    private TransactionService transactionService;
//    @Mock
//    private QueueService dataPublicationQueue;
//    @InjectMocks
//    private NotificationProcessorServiceImpl notificationProcessorService;
//
//    private final Transaction transaction = Transaction.builder()
//            .id(UUID.randomUUID())
//            .transactionId("testProcessId")
//            .createdAt(Timestamp.from(Instant.now()))
//            .entityId("testId")
//            .entityType("testType")
//            .entityHash("testHash")
//            .status(TransactionStatus.PUBLISHED)
//            .trader(TransactionTrader.CONSUMER)
//            .entityHash("testHash")
//            .datalocation("testLocation?hl=testHash")
//            .build();
//    private final Mono<Transaction> emptyTransaction = Mono.empty().cast(Transaction.class);
//    private BlockchainNotification blockchainNotification = BlockchainNotification.builder().dataLocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:9?hl=0xb0a5c22d46e83a3c5633460214254952019022db09251a858975d16548164ae8").build();
//    private BrokerNotification brokerNotification = BrokerNotification.builder().data(List.of(Map.of("id", "testId"))).build();
//
////    @Test
////    void processBrokerNotification() {
////        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString()))
////                .thenReturn(Mono.empty());
////        when(dataPublicationQueue.enqueueEvent(any(EventQueue.class)))
////                .thenReturn(Mono.empty());
////        StepVerifier.create(notificationProcessorService.processBrokerNotification("processId", brokerNotification))
////                .verifyComplete();
////        verify(transactionService).findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString());
////        verify(dataPublicationQueue).enqueueEvent(any(EventQueue.class));
////    }
//
////    @Test
////    void processDLTNotification() {
////        when(dataPublicationQueue.enqueueEvent(any(EventQueue.class))).thenReturn(Mono.empty());
////        StepVerifier.create(notificationProcessorService.processDLTNotification("processId", dltNotification))
////                .verifyComplete();
////    }
//
////    @Test
////    void processBrokerNotification_ValidData() throws JsonProcessingException {
////        // Arrange
////        Map<String, Object> dataMap = new HashMap<>();
////        dataMap.put("id", "testId");
////        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString()))
////                .thenReturn(Mono.just(transaction));
////        when(objectMapper.writer()).thenReturn(objectWriter);
////        when(objectWriter.writeValueAsString(dataMap)).thenReturn("{\"id\":\"testId\"}");
////        // Act & Assert
////        StepVerifier.create(notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification))
////                .expectNextMatches(returnedDataMap -> returnedDataMap.equals(dataMap))
////                .verifyComplete();
////    }
//
////    @Test
////    void processBrokerNotification_ValidData_selfGenerated() throws JsonProcessingException, NoSuchAlgorithmException {
////        // Arrange
////        Map<String, Object> dataMap = new HashMap<>();
////        dataMap.put("id", "testId");
////        String brokerEntityAsAString = "{\"id\":\"testId\"}";
////        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString()))
////                .thenReturn(Mono.just(transaction));
////        when(objectMapper.writer()).thenReturn(objectWriter);
////        when(objectWriter.writeValueAsString(dataMap)).thenReturn(brokerEntityAsAString);
////        try (MockedStatic<ApplicationUtils> utilities = Mockito.mockStatic(ApplicationUtils.class)) {
////            utilities.when(() -> ApplicationUtils.calculateSHA256(brokerEntityAsAString)).thenReturn("testHash");
////            // Act & Assert
////            StepVerifier.create(notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification))
////                    .verifyComplete();
////        }
////    }
//
//    @Test
//    void processBrokerNotification_InvalidData() {
//        // Arrange invalid brokerNotification
//        brokerNotification = mock(BrokerNotification.class);
//        when(brokerNotification.data()).thenReturn(Collections.emptyList());
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () ->
//                notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification));
//    }
//
//    @Test
//    void testNullNotificationData() {
//        // Arrange
//        List<Map<String, Object>> mockList = mock(List.class);
//        when(mockList.get(0)).thenReturn(null);
//        BrokerNotification brokerNotification = mock(BrokerNotification.class);
//        when(brokerNotification.data()).thenReturn(mockList);
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> {
//            notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification);
//        });
//    }
//
//
////    @Test
////    void testBrokerFromExternalSource() throws JsonProcessingException {
////        // Arrange
////        Map<String, Object> dataMap = new HashMap<>();
////        dataMap.put("id", "testId");
////        Transaction transactionFound = mock(Transaction.class);
////        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString()))
////                .thenReturn(emptyTransaction);
////        when(objectMapper.writer()).thenReturn(objectWriter);
////        when(objectWriter.writeValueAsString(dataMap)).thenReturn("{\"id\":\"testId\"}");
////        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
////            applicationUtils
////                    .when(() -> calculateSHA256(anyString()))
////                    .thenReturn("testHash");
////            // Act & Assert
////            StepVerifier.create(notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification))
////                    .expectNext(dataMap)
////                    .verifyComplete();
////        }
////    }
//
////    @Test
////    void testTransactionError() throws JsonProcessingException {
////        // Arrange
////        Map<String, Object> dataMap = new HashMap<>();
////        dataMap.put("id", "testId");
////        Transaction transactionFound = mock(Transaction.class);
////        when(transactionService.findLatestPublishedOrDeletedTransactionForEntity(anyString(), anyString()))
////                .thenReturn(Mono.just(transaction));
////        when(objectMapper.writer()).thenReturn(objectWriter);
////        when(objectWriter.writeValueAsString(dataMap)).thenThrow(new JsonProcessingException("") {
////        });
////        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
////            applicationUtils
////                    .when(() -> calculateSHA256(anyString()))
////                    .thenReturn("testHash");
////            // Act & Assert
////            StepVerifier.create(notificationProcessorService.processBrokerNotification("testProcessId", brokerNotification))
////                    .verifyError(BrokerNotificationParserException.class);
////        }
////    }
//
////    @Test
////    void processBlockchainNotification_ValidNotification() {
////        // Arrange
////        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
////        // Act & Assert
////        StepVerifier.create(notificationProcessorService.processDLTNotification("testProcessId", dltNotification))
////                .verifyComplete();
////    }
//
//    @Test
//    void processBlockchainNotification_InvalidNotification() {
//        // Arrange
//        blockchainNotification = mock(BlockchainNotification.class);
//        when(blockchainNotification.dataLocation()).thenReturn("");
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> notificationProcessorService.processDLTNotification("testProcessId", blockchainNotification));
//    }
//
//}
