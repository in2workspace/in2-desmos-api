//package es.in2.desmos.api.service;
//
//import es.in2.desmos.api.model.BlockchainNotification;
//import es.in2.desmos.api.service.impl.BrokerEntityRetrievalServiceImpl;
//import es.in2.desmos.api.util.ApplicationUtils;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class BrokerEntityRetrievalServiceTests {
//
//    private static MockWebServer mockWebServer;
//    private final BlockchainNotification notification = BlockchainNotification.builder()
//            .id(BlockchainNotification.Id.builder()
//                    .build())
//            .dataLocation("http://testbroker.com/entity?hl=hash123")
//            .eventType("EntityCreated")
//            .publisherAddress("http://testbroker.com")
//            .relevantMetadata(List.of())
//            .build();
//    @Mock
//    private TransactionService transactionService;
//
//    @InjectMocks
//    private BrokerEntityRetrievalServiceImpl brokerEntityRetrievalService;
//
//    @AfterAll
//    public static void tearDown() throws IOException {
//
//        mockWebServer.shutdown();
//    }
//
//    @BeforeEach
//    void setUp() throws IOException {
//        mockWebServer = new MockWebServer();
//        mockWebServer.start();
//        WebClient webClient = WebClient.builder()
//                .baseUrl(mockWebServer.url("/")
//                        .toString())
//                .build();
//    }
//
//    @Test
//    void retrieveEntityFromSourceBroker_Success() {
//        // Arrange
//        String expectedResponse = "Entity Data";
//        mockWebServer.enqueue(new MockResponse()
//                .setResponseCode(200)
//                .setBody(expectedResponse));
//        when(transactionService.saveTransaction(anyString(), any())).thenReturn(Mono.empty());
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils.when(() -> ApplicationUtils.extractEntityUrlFromDataLocation(anyString()))
//                    .thenReturn("http://testbroker.com/entity");
//            // Act & Assert
//            Mono<String> result = brokerEntityRetrievalService.retrieveEntityFromSourceBroker("testProcessId", notification);
//            StepVerifier.create(result)//TODO: verificar como pasar la baseUrl de mi webclient al metodo de la prueba
//                    .expectNext("Entity Data")
//                    .verifyComplete();
//        }
//    }
//}

//    @Test
//    void retrieveEntityFromSourceBroker_EntityDeleted() {
//        when((Publisher<?>) responseSpec.onStatus(eq(HttpStatus.NOT_FOUND::equals), any())).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.empty());
//
//        Mono<String> result = brokerEntityRetrievalService.retrieveEntityFromSourceBroker("testProcessId", notification);
//
//        StepVerifier.create(result)
//                .verifyComplete();
//
//        verify(transactionService).saveTransaction(eq("testProcessId"), any(Transaction.class));
//    }