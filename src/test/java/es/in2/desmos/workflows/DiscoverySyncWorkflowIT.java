package es.in2.desmos.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.objectmothers.DiscoverySyncRequestMother;
import es.in2.desmos.objectmothers.DiscoverySyncResponseMother;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DiscoverySyncWorkflowIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void itShouldUpdateAndCreateLocalEntitiesBasedOnExternalEntitiesAndReturnMissingExternalEntities() throws JsonProcessingException {
        var discoverySyncRequest = DiscoverySyncRequestMother.simpleDiscoverySyncRequest();
        var discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);

        var discoverySyncResponse = DiscoverySyncResponseMother.fullDiscoverySyncResponse();
        var discoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        var brokerUrl = ContainerManager.getBaseUriForScorpioA();
        AddInitialEntitiesToContextBroker(brokerUrl);


        webTestClient.post()
                .uri("/api/v1/sync/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(discoverySyncRequestJson)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(discoverySyncResponseJson)
                .consumeWith(System.out::println);


        webTestClient.post()
                .uri("/api/v1/sync/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(discoverySyncRequestJson)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(discoverySyncResponseJson)
                .consumeWith(System.out::println);
    }

    private void AddInitialEntitiesToContextBroker(String brokerUrl) {

        var webClient =
                WebClient.builder()
                        .baseUrl(brokerUrl)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build()
                        .post()
                        .uri("ngsi-ld/v1/entities/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .retry(3);
    }
}
