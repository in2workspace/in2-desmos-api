package es.in2.desmos.infrastructure.controllers;

import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
        Mono<List<Id>> entitiesIdsMono = Mono.just(List.of(new Id("123")));
        List<Entity> expectedEntitiesList = List.of(new Entity("Entity details"));
        when(brokerPublisherService.findEntitiesAndItsSubentitiesByIdInBase64(anyString(), any(), any()))
                .thenReturn(Mono.just(expectedEntitiesList));

        webTestClient
                .get()
                .uri("/api/v1/entities/{id}", entitiesIdsMono)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Entity>>() {})
                .isEqualTo(expectedEntitiesList);
    }
}