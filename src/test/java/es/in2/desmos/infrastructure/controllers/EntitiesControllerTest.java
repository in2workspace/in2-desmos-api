package es.in2.desmos.infrastructure.controllers;

import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(EntitiesController.class)
@WithMockUser
class EntitiesControllerTest {

    @MockBean
    private BrokerPublisherService brokerPublisherService;

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void testGetEntitiesSuccess() {
        String entityId = "123";
        String expectedResponse = "Entity details";
        when(brokerPublisherService.getEntityById(anyString(), eq(entityId)))
                .thenReturn(Mono.just(expectedResponse));

        webTestClient
                .get()
                .uri("/api/v1/entities/{id}", entityId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedResponse);
    }
}