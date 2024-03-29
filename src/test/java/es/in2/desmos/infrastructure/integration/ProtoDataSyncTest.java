//package es.in2.desmos.infrastructure.integration;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import es.in2.desmos.application.service.DataPublicationService;
//import es.in2.desmos.application.service.DataRetrievalService;
//import es.in2.desmos.application.todo.BlockchainToBrokerDataSyncSynchronizer;
//import es.in2.desmos.application.todo.BrokerToBlockchainDataSyncPublisher;
//import es.in2.desmos.application.todo.ProtoDataSync;
//import es.in2.desmos.domain.model.Transaction;
//import es.in2.desmos.domain.model.TransactionStatus;
//import es.in2.desmos.domain.model.TransactionTrader;
//import es.in2.desmos.domain.service.TransactionService;
//import es.in2.desmos.infrastructure.blockchain.service.DLTAdapterPublisher;
//import es.in2.desmos.infrastructure.broker.service.BrokerPublicationService;
//import es.in2.desmos.infrastructure.configs.properties.DLTAdapterProperties;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.Disposable;
//import reactor.core.publisher.Flux;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class ProtoDataSyncTest {
//
//    @Mock
//    private TransactionService transactionService;
//    @Mock
//    private DLTAdapterProperties DLTAdapterProperties;
//    @Mock
//    private DLTAdapterPublisher DLTAdapterPublisher;
//    @Mock
//    private ObjectMapper objectMapper;
//    @Mock
//    private BrokerToBlockchainDataSyncPublisher brokerToBlockchainDataSyncPublisher;
//    @Mock
//    private BlockchainToBrokerDataSyncSynchronizer blockchainToBrokerDataSyncSynchronizer;
//    @Mock
//    private DataPublicationService brokerToBlockchainPublisher;
//    @Mock
//    private DataRetrievalService blockchainToBrokerSynchronizer;
//    @Mock
//    private BrokerPublicationService brokerPublicationService;
//
//    @Mock
//    private WebClient webClientMock;
//
//    @Mock
//    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
//
//    @Mock
//    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
//
//    @Mock
//    private WebClient.ResponseSpec responseSpecMock;
//
//    @InjectMocks
//    private ProtoDataSync protoDataSync;
//
////    @BeforeEach
////    void setUp() {
////        // Corrected to include all dependencies
////        DLTAdapterProperties = new DLTAdapterProperties("http://localhost:8080", "http://localhost:8080", "http" +
////                "://localhost:8080", new DLTAdapterProperties.DLTAdapterPathProperties("/configureNode", "/publish", "/subscribe"));
////        protoDataSync = new ProtoDataSync(transactionService, DLTAdapterProperties,
////                DLTAdapterPublisher, new ObjectMapper(), brokerToBlockchainDataSyncPublisher,
////                blockchainToBrokerDataSyncSynchronizer, brokerToBlockchainPublisher, blockchainToBrokerSynchronizer, brokerPublicationService);
////    }
//
////    @Test
////    void extractIdsBasedOnPositionShouldReturnListOfIds() throws Exception {
////        //Given
////        String json = "[{\"id\":\"1\"}, {\"id\":\"2\"}, {\"id\":\"3\"}]";
////        List<String> expectedIds = List.of("1", "2", "3");
////        JsonNode rootNode = objectMapper.readTree(json);
////        when(objectMapper.readTree(json)).thenReturn(rootNode);
////        //When
////        List<String> actualIds = protoDataSync.extractIdsBasedOnPosition(json);
////        //Then
////        assertEquals(expectedIds, actualIds, "The extracted IDs should match the expected ones.");
////    }
//
//
//    @Test
//    void testProcessConsumerTransactionWithTransactions() {
//        // Given
//        Transaction lastTransactionPublished = Transaction.builder()
//                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f5")
//                .createdAt(Timestamp.from(Instant.now()))
//                .datalocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:1234")
//                .entityId("urn:ngsi-ld:Entity:1234")
//                .entityType("Entity")
//                .entityHash("0x1234")
//                .status(TransactionStatus.PUBLISHED)
//                .trader(TransactionTrader.CONSUMER)
//                .entityHash("0x9876")
//                .build(); // Populate this as needed
//        Transaction lastTransactionCreated = Transaction.builder()
//                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f6")
//                .createdAt(Timestamp.from(Instant.now()))
//                .datalocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:12345")
//                .entityId("urn:ngsi-ld:Entity:12345")
//                .entityType("Entity")
//                .entityHash("0x1235")
//                .status(TransactionStatus.CREATED)
//                .trader(TransactionTrader.CONSUMER)
//                .build();
//        lastTransactionPublished.setCreatedAt(Timestamp.valueOf("2024-02-06 10:07:01.529"));
//        List<Transaction> transactionList = List.of(lastTransactionPublished, lastTransactionCreated);
//
//        Flux<Transaction> transactionsFlux = Flux.fromIterable(transactionList);
//
//        // Assuming queryDLTAdapterFromRange returns an empty Flux for simplicity
//
//        when(transactionService.getAllTransactions(any())).thenReturn(transactionsFlux);
//        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
//        when(requestHeadersUriSpecMock.uri((String) any())).thenReturn(requestHeadersSpecMock);
//        when(requestHeadersSpecMock.accept((MediaType) any())).thenReturn(requestHeadersSpecMock);
//        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
//        String dltNotificationDTOexample = "{\"id\":1,\"publisherAddress\":\"String\",\"eventType\":\"ProductOffering\"," +
//                "\"timestamp\":3,\"dataLocation\":\"http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:443734333" +
//                "?hl=0xd6e502951d411812220f92c9eb3795eb2674aa71918128f19daf296deb40942\",\"relevantMetadata\":[]}";
//        when(DLTAdapterPublisher.getEventsFromRange(any(String.class), any(Long.class), any(Long.class)))
//                .thenReturn(Flux.just(dltNotificationDTOexample));
//        Flux<String> mockResponse = Flux.just(dltNotificationDTOexample);
//        when(responseSpecMock.bodyToFlux(String.class)).thenReturn(mockResponse);
//
//
//        // Then
//        protoDataSync.processAllTransactions();
//
//        verify(transactionService, times(1)).getAllTransactions(any());
//
//
//    }
//
//    @Test
//    void testGetPreviousTransactions() {
//        // Given
//        Transaction lastTransactionPublished = Transaction.builder()
//                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f5")
//                .createdAt(Timestamp.from(Instant.now()))
//                .datalocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:1234")
//                .entityId("urn:ngsi-ld:Entity:1234")
//                .entityType("Entity")
//                .entityHash("0x1234")
//                .status(TransactionStatus.PUBLISHED)
//                .trader(TransactionTrader.CONSUMER)
//                .entityHash("0x9876")
//                .build(); // Populate this as needed
//        Transaction lastTransactionCreated = Transaction.builder()
//                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f6")
//                .createdAt(Timestamp.from(Instant.now()))
//                .datalocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:12345")
//                .entityId("urn:ngsi-ld:Entity:12345")
//                .entityType("Entity")
//                .entityHash("0x1235")
//                .status(TransactionStatus.CREATED)
//                .trader(TransactionTrader.CONSUMER)
//                .entityHash("0x9876")
//                .build();
//        lastTransactionPublished.setCreatedAt(Timestamp.valueOf("2024-02-06 10:07:01.529"));
//        List<Transaction> transactionList = List.of(lastTransactionPublished, lastTransactionCreated);
//
//        Flux<Transaction> transactionsFlux = Flux.fromIterable(transactionList);
//
//        // Assuming queryDLTAdapterFromRange returns an empty Flux for simplicity
//
//        when(transactionService.getAllTransactions(any())).thenReturn(transactionsFlux);
////        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
//        when(requestHeadersUriSpecMock.uri((String) any())).thenReturn(requestHeadersSpecMock);
//        when(requestHeadersSpecMock.accept((MediaType) any())).thenReturn
//                (requestHeadersSpecMock);
//        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
//
//
//        String dltNotificationDTOexample = "{\"id\":1,\"publisherAddress\":\"String\",\"eventType\":\"ProductOffering\"," +
//                "\"timestamp\":3,\"dataLocation\":\"http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:443734333"
//                +
//                "?hl=0xd6e502951d411812220f92c9eb3795eb2674aa71918128f19daf296deb40942\",\"relevantMetadata\":[]}";
//        when(DLTAdapterPublisher.getEventsFromRange(any(String.class), any(Long.class), any(Long.class)))
//                .thenReturn(Flux.just(dltNotificationDTOexample));
//        Flux<String> mockResponse = Flux.just(dltNotificationDTOexample);
//        when(responseSpecMock.bodyToFlux(String.class)).thenReturn(mockResponse);
//
//        // When
//        protoDataSync.processAllTransactions();
//
//        // Then
//        verify(transactionService, times(1)).getAllTransactions(any());
//    }
//
//    @Test
//    void cleanUpShouldDisposeResources() {
//        // Simula suscripciones activas
//        Disposable blockchainEventSubscription = mock(Disposable.class);
//        Disposable brokerEntityEventSubscription = mock(Disposable.class);
//        when(blockchainEventSubscription.isDisposed()).thenReturn(false);
//        when(brokerEntityEventSubscription.isDisposed()).thenReturn(false);
//
//        // Asigna las suscripciones simuladas
//        ReflectionTestUtils.setField(protoDataSync, "blockchainEventProcessingSubscription",
//                blockchainEventSubscription);
//        ReflectionTestUtils.setField(protoDataSync, "brokerEntityEventProcessingSubscription",
//                brokerEntityEventSubscription);
//
//        // Ejecuta cleanUp
//        protoDataSync.cleanUp();
//
//        // Verificaciones
//        verify(blockchainEventSubscription).dispose();
//        verify(brokerEntityEventSubscription).dispose();
//    }
//
//
//}
