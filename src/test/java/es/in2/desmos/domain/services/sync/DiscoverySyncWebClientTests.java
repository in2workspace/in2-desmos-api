package es.in2.desmos.domain.services.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.services.sync.impl.DiscoverySyncWebClientImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscoverySyncWebClientTests {
    @InjectMocks
    private DiscoverySyncWebClientImpl discoverySyncWebClient;

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
    void itShouldReturnEntitySyncResponseWhenMakeRequest() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        String processId = "0";

        String issuer = "http://example.org";
        Mono<String> issuerMono = Mono.just(issuer);
        var mvEntities4DataNegotiation = MVEntity4DataNegotiationMother.list1And2();

        DiscoverySyncRequest discoverySyncRequest = new DiscoverySyncRequest("http://my-domain.org", mvEntities4DataNegotiation);

        var expectedEntities = MVEntity4DataNegotiationMother.list3And4();
        DiscoverySyncResponse expectedResponse = new DiscoverySyncResponse("http://external-domain.org", expectedEntities);

        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(issuer + "/api/v1/sync/p2p/discovery")).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpecMock);
        //noinspection unchecked
        when(requestBodySpecMock.body(any(), eq(DiscoverySyncRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(DiscoverySyncResponse.class)).thenReturn(Mono.just(expectedResponse));


        Mono<DiscoverySyncResponse> result = discoverySyncWebClient.makeRequest(processId, issuerMono, Mono.just(discoverySyncRequest));

        StepVerifier
                .create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}