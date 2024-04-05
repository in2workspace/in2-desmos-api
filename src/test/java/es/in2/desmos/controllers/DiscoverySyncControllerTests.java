package es.in2.desmos.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.services.sync.DiscoverySyncService;
import es.in2.desmos.objectmothers.DiscoverySyncRequestMother;
import es.in2.desmos.objectmothers.DiscoverySyncResponseMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(DiscoverySyncController.class)
class DiscoverySyncControllerTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DiscoverySyncService service;

    @Test
    void itShouldReturnExternalEntityIdsWithIssuer() throws JsonProcessingException {

        var discoverySyncRequest = DiscoverySyncRequestMother.simpleDiscoverySyncRequest();
        var discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);

        var discoverySyncResponse = DiscoverySyncResponseMother.simpleDiscoverySyncResponse();
        var discoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        when(service.discoverySync(anyString(), eq(discoverySyncRequest.issuer()), eq(discoverySyncRequest.externalEntityIds()))).thenReturn(discoverySyncResponse.localEntitiesIds());

        webTestClient.post()
                .uri("/api/v1/sync/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(discoverySyncRequestJson)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(discoverySyncResponseJson);
    }


}
