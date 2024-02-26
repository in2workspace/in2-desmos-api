package es.in2.desmos.blockchain.adapter;

import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.service.TransactionService;
import es.in2.desmos.blockchain.config.properties.DLTAdapterPathProperties;
import es.in2.desmos.blockchain.config.properties.DLTAdapterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DLTAdapterTest {

    @Mock
    private DLTAdapterProperties DLTAdapterProperties;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    private DigitelDLTAdapter digitelDLTAdapter;


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        DLTAdapterProperties = new DLTAdapterProperties("http://localhost:8080", "http://localhost:8080", "http" +
                "://localhost:8080", new DLTAdapterPathProperties("/publish", "/publish", "/subscribe"));
        TransactionService transactionService = mock(TransactionService.class);
        WebClient webClient = mock(WebClient.class);

        digitelDLTAdapter = new DigitelDLTAdapter(DLTAdapterProperties, transactionService);
        Field webClientField = DigitelDLTAdapter.class.getDeclaredField("webClient");
        webClientField.setAccessible(true);
        webClientField.set(digitelDLTAdapter, webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(any(String.class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.accept(any(MediaType.class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentType(any(MediaType.class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

    }

    @Test
    void publishEventShouldSaveTransactionBasedOnCondition() {

        BlockchainEvent blockchainEvent = BlockchainEvent.builder()
                .eventType("eventType")
                .dataLocation("http://example.com?hl=0xd6e5")
                .previousEntityHash("")
                .organizationId("organizationId")
                .build();


        Mono<Void> result = digitelDLTAdapter.publishEvent("processId", blockchainEvent);

        StepVerifier.create(result)
                .verifyComplete();

    }

}