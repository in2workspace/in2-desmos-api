package es.in2.desmos.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.services.sync.SyncDiscoveryService;
import es.in2.desmos.objectmothers.SyncDiscoveryRequestMother;
import es.in2.desmos.objectmothers.SyncDiscoveryResponseMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(SyncDiscoveryController.class)
class SyncDiscoveryControllerTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SyncDiscoveryService service;

    @Test
    void itShouldReturnExternalEntityIdsWithIssuer() throws JsonProcessingException {

        var syncDiscoveryRequest = SyncDiscoveryRequestMother.simpleSyncDiscoveryRequest();
        var syncDiscoveryRequestJson = objectMapper.writeValueAsString(syncDiscoveryRequest);

        var syncDiscoveryResponse = SyncDiscoveryResponseMother.simpleSyncDiscoveryResponse();
        var syncDiscoveryResponseJson = objectMapper.writeValueAsString(syncDiscoveryResponse);

        when(service.syncDiscovery(anyString(), eq(syncDiscoveryRequest.issuer()), eq(syncDiscoveryRequest.externalEntityIds()))).thenReturn(syncDiscoveryResponse.localEntitiesIds());

        webTestClient.post()
                .uri("/api/v1/sync/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(syncDiscoveryRequestJson)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(syncDiscoveryResponseJson);
    }


}
