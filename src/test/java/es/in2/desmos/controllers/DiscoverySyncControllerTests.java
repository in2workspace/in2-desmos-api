package es.in2.desmos.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.objectmothers.DiscoverySyncRequestMother;
import es.in2.desmos.objectmothers.DiscoverySyncResponseMother;
import es.in2.desmos.workflows.DiscoverySyncWorkflow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(DiscoverySyncController.class)
class DiscoverySyncControllerTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DiscoverySyncWorkflow discoverySyncWorkflow;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    @Test
    void itShouldReturnExternalEntityIdsWithIssuer() throws JsonProcessingException {

        Mono<DiscoverySyncRequest> discoverySyncRequest = Mono.just(DiscoverySyncRequestMother.simpleDiscoverySyncRequest());

        DiscoverySyncResponse discoverySyncResponse = DiscoverySyncResponseMother.simpleDiscoverySyncResponse(contextBrokerExternalDomain);
        var discoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        Mono<List<ProductOffering>> localEntityIds = Mono.just(DiscoverySyncResponseMother.simpleDiscoverySyncResponse(contextBrokerExternalDomain).localEntitiesIds());
        when(discoverySyncWorkflow.discoverySync(anyString(), any(), any())).thenReturn(localEntityIds);

        webTestClient.post()
                .uri("/api/v1/sync/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .body(discoverySyncRequest, DiscoverySyncRequest.class)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(discoverySyncResponseJson)
                .consumeWith(System.out::println);

        verify(discoverySyncWorkflow, times(1)).discoverySync(anyString(), any(), any());
        verifyNoMoreInteractions(discoverySyncWorkflow);
    }


}
