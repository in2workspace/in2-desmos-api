//package es.in2.desmos.broker.adapter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import es.in2.desmos.broker.config.properties.BrokerProperties;
//import es.in2.desmos.broker.model.BrokerSubscription;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ScorpioAdapterTests {
//
//    @Mock
//    private WebClient webClient;
//
//    @Mock
//    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;
//
//    @Mock
//    private WebClient.RequestBodySpec requestBodySpecMock;
//
//    @Mock
//    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
//
//    @Mock
//    private WebClient.ResponseSpec responseSpecMock;
//
//    @Mock
//    private ObjectMapper objectMapper;
//    @Mock
//    private BrokerProperties brokerProperties;
//
//    @InjectMocks
//    private ScorpioAdapter scorpioAdapter;
//
//    @BeforeEach
//    void setUp() {
//        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
//        when(requestBodyUriSpecMock.uri(any(String.class))).thenReturn(requestBodySpecMock);
//        when(requestBodySpecMock.accept(any(MediaType.class))).thenReturn(requestBodySpecMock);
//        when(requestBodySpecMock.contentType(any(MediaType.class))).thenReturn(requestBodySpecMock);
//        when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
//        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
//        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());
//        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
//        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());
//
//    }
//
//
//    @Test
//    void updateSubscriptionShouldCompleteWithoutError() {
//        // Preparar
//        BrokerSubscription brokerSubscription = new BrokerSubscription(); // Asegúrate de configurar los valores necesarios en
//        // brokerSubscription
//        String processId = "processId";
//
//        // Configurar el comportamiento del mock
//        when(webClient.patch()).thenReturn(requestBodyUriSpecMock);
//        when(requestBodyUriSpecMock.uri(any(String.class))).thenReturn(requestBodySpecMock);
//        when(requestBodySpecMock.accept(any(MediaType.class))).thenReturn(requestBodySpecMock);
//        when(requestBodySpecMock.contentType(any(MediaType.class))).thenReturn(requestBodySpecMock);
//        when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
//        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
//        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());
//
//        // Ejecutar
//        Mono<Void> result = digitelBlockchainAdapter.updateSubscription(processId, brokerSubscription);
//
//        // Verificar
//        StepVerifier.create(result)
//                .verifyComplete();
//    }
//
//    @Test
//    void deleteSubscriptionShouldCompleteWithoutError() {
//        // Preparar
//        String processId = "processId";
//        String subscriptionId = "subscriptionId";
//
//        // Ejecutar
//        Mono<Void> result = digitelBlockchainAdapter.deleteSubscription(processId, subscriptionId);
//
//        // Verificar
//        StepVerifier.create(result)
//                .verifyComplete();
//    }
//
//
//}