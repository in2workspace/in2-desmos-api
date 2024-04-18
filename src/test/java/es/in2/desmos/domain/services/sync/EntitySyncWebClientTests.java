package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.sync.impl.EntitySyncWebClientImpl;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntitySyncWebClientTests {
    @InjectMocks
    private EntitySyncWebClientImpl entitySyncWebClient;

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodyMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Test
    void itShouldReturnEntitySyncResponseWhenMakeRequest() {
        Mono<String> issuer = Mono.just("http://example.org");
        Mono<MVEntity4DataNegotiation[]> entitySyncRequest = Mono.just(MVEntity4DataNegotiationMother.fullList().toArray(MVEntity4DataNegotiation[]::new));

        String expectedResult = (EntitySyncResponseMother.sample());


        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(issuer + "/api/v1/sync/entities")).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpecMock);
        //noinspection unchecked
        when(requestBodySpecMock.body(entitySyncRequest, MVEntity4DataNegotiation[].class)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(expectedResult));


        Mono<String> result = entitySyncWebClient.makeRequest(issuer, entitySyncRequest);

        StepVerifier
                .create(result)
                .expectNext(expectedResult)
                .verifyComplete();
    }
}