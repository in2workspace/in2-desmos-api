package es.in2.desmos.api.service;//package es.in2.connector.api.service;
//
//import es.in2.connector.api.model.BlockchainNotification;
//import es.in2.connector.api.model.Transaction;
//import es.in2.connector.api.service.impl.BrokerEntityRetrievalServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.reactivestreams.Publisher;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.verify;
//import static reactor.core.publisher.Mono.when;
//
//@ExtendWith(MockitoExtension.class)
//class BrokerEntityRetrievalServiceTests {
//
//    @Mock
//    private TransactionService transactionService;
//
//    @Mock
//    private WebClient webClient;
//
//    @Mock
//    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//
//    @Mock
//    private WebClient.RequestHeadersSpec requestHeadersSpec;
//
//    @Mock
//    private WebClient.ResponseSpec responseSpec;
//
//    @InjectMocks
//    private BrokerEntityRetrievalServiceImpl brokerEntityRetrievalService;
//
//    private final BlockchainNotification notification = BlockchainNotification.builder()
//            .id(BlockchainNotification.Id.builder().build())
//            .dataLocation("http://testbroker.com/entity123")
//            .eventType("EntityCreated")
//            .publisherAddress("http://testbroker.com")
//            .relevantMetadata(List.of())
//            .build();
//
//    @BeforeEach
//    void setUp() {
//        when((Publisher<?>) webClient.get()).thenReturn(requestHeadersUriSpec);
//        when((Publisher<?>) requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
//        when((Publisher<?>) requestHeadersSpec.accept(any(MediaType.class))).thenReturn(requestHeadersSpec);
//        when((Publisher<?>) requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//    }
//
//    @Test
//    void retrieveEntityFromSourceBroker_Success() {
//        when((Publisher<?>) responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Entity Data"));
//
//        Mono<String> result = brokerEntityRetrievalService.retrieveEntityFromSourceBroker("testProcessId", notification);
//
//        StepVerifier.create(result)
//                .expectNext("Entity Data")
//                .verifyComplete();
//    }
//
//    @Test
//    void retrieveEntityFromSourceBroker_EntityDeleted() {
//        when((Publisher<?>) responseSpec.onStatus(eq(HttpStatus.NOT_FOUND::equals), any())).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.empty());
//        when(transactionService.saveTransaction(anyString(), any(Transaction.class)))
//                .thenReturn(Mono.empty());
//
//        Mono<String> result = brokerEntityRetrievalService.retrieveEntityFromSourceBroker("testProcessId", notification);
//
//        StepVerifier.create(result)
//                .verifyComplete();
//
//        verify(transactionService).saveTransaction(eq("testProcessId"), any(Transaction.class));
//    }
//
//}
