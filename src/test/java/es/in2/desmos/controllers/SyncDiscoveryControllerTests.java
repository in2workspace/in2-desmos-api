package es.in2.desmos.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.objectmothers.SyncDiscoveryRequestMother;
import es.in2.desmos.objectmothers.SyncDiscoveryResponseMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(SyncDiscoveryController.class)
class SyncDiscoveryControllerTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void itShouldReturnExternalEntityIdsWithIssuer() {

        var syncDiscoveryRequest = SyncDiscoveryRequestMother.simpleSyncDiscoveryRequest();
        var syncDiscoveryRequestJson = objectMapper.writeValueAsString(syncDiscoveryRequest);

        var syncDiscoveryResponse = SyncDiscoveryResponseMother.simpleSyncDiscoveryResponse();
        var syncDiscoveryResponseJson = objectMapper.writeValueAsString(syncDiscoveryResponse);

        webTestClient.post()
                .uri("/sync/discovery")
                .accept(MediaType.APPLICATION_JSON)
                .body(syncDiscoveryRequestJson)
                .exchange()
                .expectStatus().isAccepted()
                .getResponseBody()
                .json(syncDiscoveryResponseJson);
    }


}
