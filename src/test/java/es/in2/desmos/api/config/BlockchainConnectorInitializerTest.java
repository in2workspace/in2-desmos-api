package es.in2.desmos.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.api.facade.BlockchainToBrokerDataSyncSynchronizer;
import es.in2.desmos.api.facade.BlockchainToBrokerSynchronizer;
import es.in2.desmos.api.facade.BrokerToBlockchainDataSyncPublisher;
import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.service.TransactionService;
import es.in2.desmos.blockchain.config.properties.DLTAdapterPathProperties;
import es.in2.desmos.blockchain.config.properties.DLTAdapterProperties;
import es.in2.desmos.blockchain.service.DLTAdapterEventPublisher;
import es.in2.desmos.broker.service.BrokerPublicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BlockchainConnectorInitializerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private DLTAdapterProperties DLTAdapterProperties;

    @Mock
    private DLTAdapterEventPublisher DLTAdapterEventPublisher;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BrokerToBlockchainPublisher brokerToBlockchainPublisher;

    @Mock
    private BrokerToBlockchainDataSyncPublisher brokerToBlockchainDataSyncPublisher;

    @Mock
    private BlockchainToBrokerSynchronizer blockchainToBrokerSynchronizer;

    @Mock
    private BlockchainToBrokerDataSyncSynchronizer blockchainToBrokerDataSyncSynchronizer;

    @Mock
    private BrokerPublicationService brokerPublicationService;

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private BlockchainConnectorInitializer blockchainConnectorInitializer;

    @BeforeEach
    void setUp() {
        // Corrected to include all dependencies
        DLTAdapterProperties = new DLTAdapterProperties("http://localhost:8080", "http://localhost:8080", "http" +
                "://localhost:8080", new DLTAdapterPathProperties("/configureNode", "/publish", "/subscribe"));
        blockchainConnectorInitializer = new BlockchainConnectorInitializer(transactionService, DLTAdapterProperties,
                DLTAdapterEventPublisher, new ObjectMapper(), brokerToBlockchainDataSyncPublisher,
                brokerToBlockchainPublisher, blockchainToBrokerSynchronizer, blockchainToBrokerDataSyncSynchronizer,
                brokerPublicationService);
    }

    @Test
    void extractIdsBasedOnPositionShouldReturnListOfIds() throws Exception {
        //Given
        String json = "[{\"id\":\"1\"}, {\"id\":\"2\"}, {\"id\":\"3\"}]";
        List<String> expectedIds = List.of("1", "2", "3");
        JsonNode rootNode = objectMapper.readTree(json);
        when(objectMapper.readTree(json)).thenReturn(rootNode);

        //When
        List<String> actualIds = blockchainConnectorInitializer.extractIdsBasedOnPosition(json);


        //Then
        assertEquals(expectedIds, actualIds, "The extracted IDs should match the expected ones.");
    }


    @Test
    void testProcessConsumerTransactionWithTransactions() {
        // Given
        Transaction lastTransactionPublished = Transaction.builder()
                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f5")
                .createdAt(Timestamp.from(Instant.now()))
                .dataLocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:1234")
                .entityId("urn:ngsi-ld:Entity:1234")
                .entityType("Entity")
                .entityHash("0x1234")
                .status(TransactionStatus.PUBLISHED)
                .trader(TransactionTrader.CONSUMER)
                .hash("0x9876")
                .build(); // Populate this as needed
        Transaction lastTransactionCreated = Transaction.builder()
                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f6")
                .createdAt(Timestamp.from(Instant.now()))
                .dataLocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:12345")
                .entityId("urn:ngsi-ld:Entity:12345")
                .entityType("Entity")
                .entityHash("0x1235")
                .status(TransactionStatus.CREATED)
                .trader(TransactionTrader.CONSUMER)
                .hash("0x9876")
                .build();
        lastTransactionPublished.setCreatedAt(Timestamp.valueOf("2024-02-06 10:07:01.529"));
        List<Transaction> transactionList = List.of(lastTransactionPublished, lastTransactionCreated);

        Flux<Transaction> transactionsFlux = Flux.fromIterable(transactionList);

        // Assuming queryDLTAdapterFromRange returns an empty Flux for simplicity

        when(transactionService.getAllTransactions(any())).thenReturn(transactionsFlux);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((String) org.mockito.ArgumentMatchers.any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.accept((org.springframework.http.MediaType) org.mockito.ArgumentMatchers.any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        String dltNotificationDTOexample = "{\"id\":1,\"publisherAddress\":\"String\",\"eventType\":\"ProductOffering\"," +
                "\"timestamp\":3,\"dataLocation\":\"http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:443734333" +
                "?hl=0xd6e502951d411812220f92c9eb3795eb2674aa71918128f19daf296deb40942\",\"relevantMetadata\":[]}";
        when(DLTAdapterEventPublisher.getEventsFromRange(any(String.class), any(Long.class), any(Long.class)))
                .thenReturn(Flux.just(dltNotificationDTOexample));
        Flux<String> mockResponse = Flux.just(dltNotificationDTOexample);
        when(responseSpecMock.bodyToFlux(String.class)).thenReturn(mockResponse);


        // Then
        blockchainConnectorInitializer.processAllTransactions();

        verify(transactionService, times(1)).getAllTransactions(any());


    }

    @Test
    void testGetPreviousTransactions() {
        // Given
        Transaction lastTransactionPublished = Transaction.builder()
                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f5")
                .createdAt(Timestamp.from(Instant.now()))
                .dataLocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:1234")
                .entityId("urn:ngsi-ld:Entity:1234")
                .entityType("Entity")
                .entityHash("0x1234")
                .status(TransactionStatus.PUBLISHED)
                .trader(TransactionTrader.CONSUMER)
                .hash("0x9876")
                .build(); // Populate this as needed
        Transaction lastTransactionCreated = Transaction.builder()
                .transactionId("e1e07f6d-e8e7-48ae-bb4d-afab5b63c1f6")
                .createdAt(Timestamp.from(Instant.now()))
                .dataLocation("https://domain.org/ngsi-ld/v1/entities/urn:ngsi-ld:Entity:12345")
                .entityId("urn:ngsi-ld:Entity:12345")
                .entityType("Entity")
                .entityHash("0x1235")
                .status(TransactionStatus.CREATED)
                .trader(TransactionTrader.CONSUMER)
                .hash("0x9876")
                .build();
        lastTransactionPublished.setCreatedAt(Timestamp.valueOf("2024-02-06 10:07:01.529"));
        List<Transaction> transactionList = List.of(lastTransactionPublished, lastTransactionCreated);

        Flux<Transaction> transactionsFlux = Flux.fromIterable(transactionList);

        // Assuming queryDLTAdapterFromRange returns an empty Flux for simplicity

        when(transactionService.getAllTransactions(any())).thenReturn(transactionsFlux);
//        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((String) org.mockito.ArgumentMatchers.any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.accept((org.springframework.http.MediaType) org.mockito.ArgumentMatchers.any())).thenReturn
                (requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);


        String dltNotificationDTOexample = "{\"id\":1,\"publisherAddress\":\"String\",\"eventType\":\"ProductOffering\"," +
                "\"timestamp\":3,\"dataLocation\":\"http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:443734333"
                +
                "?hl=0xd6e502951d411812220f92c9eb3795eb2674aa71918128f19daf296deb40942\",\"relevantMetadata\":[]}";
        when(DLTAdapterEventPublisher.getEventsFromRange(any(String.class), any(Long.class), any(Long.class)))
                .thenReturn(Flux.just(dltNotificationDTOexample));
        Flux<String> mockResponse = Flux.just(dltNotificationDTOexample);
        when(responseSpecMock.bodyToFlux(String.class)).thenReturn(mockResponse);

        // When
        blockchainConnectorInitializer.processAllTransactions();

        // Then
        verify(transactionService, times(1)).getAllTransactions(any());
    }

    @Test
    void cleanUpShouldDisposeResources() {
        // Simula suscripciones activas
        Disposable blockchainEventSubscription = mock(Disposable.class);
        Disposable brokerEntityEventSubscription = mock(Disposable.class);
        when(blockchainEventSubscription.isDisposed()).thenReturn(false);
        when(brokerEntityEventSubscription.isDisposed()).thenReturn(false);

        // Asigna las suscripciones simuladas
        ReflectionTestUtils.setField(blockchainConnectorInitializer, "blockchainEventProcessingSubscription",
                blockchainEventSubscription);
        ReflectionTestUtils.setField(blockchainConnectorInitializer, "brokerEntityEventProcessingSubscription",
                brokerEntityEventSubscription);

        // Ejecuta cleanUp
        blockchainConnectorInitializer.cleanUp();

        // Verificaciones
        verify(blockchainEventSubscription).dispose();
        verify(brokerEntityEventSubscription).dispose();
    }


}