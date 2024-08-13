package es.in2.desmos.domain.services.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.services.sync.impl.EntitySyncWebClientImpl;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.NoSuchAlgorithmException;

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
    void itShouldReturnEntitySyncResponseWhenMakeRequest() throws JsonProcessingException, JSONException, NoSuchAlgorithmException {
        String processId = "0";

        String issuer = "http://example.org";
        Mono<String> issuerMono = Mono.just(issuer);
        Mono<Id[]> entitySyncRequest = Mono.just(MVEntity4DataNegotiationMother.fullList().stream().map(x -> new Id(x.id())).toArray(Id[]::new));

        String expectedResult = (EntityMother.getFullJsonList());


        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(issuer + "/api/v1/sync/p2p/entities")).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpecMock);
        //noinspection unchecked
        when(requestBodySpecMock.body(entitySyncRequest, Id[].class)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(expectedResult));


        Mono<String> result = entitySyncWebClient.makeRequest(processId, issuerMono, entitySyncRequest);

        StepVerifier
                .create(result)
                .expectNext(expectedResult)
                .verifyComplete();
    }
}