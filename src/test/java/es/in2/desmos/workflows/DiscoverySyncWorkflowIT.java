package es.in2.desmos.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.objectmothers.DiscoverySyncRequestMother;
import es.in2.desmos.objectmothers.DiscoverySyncResponseMother;
import es.in2.desmos.objectmothers.EntityMother;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DiscoverySyncWorkflowIT {

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int localServerPort;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    private static List<Entity> initialEntities;

    @BeforeAll
    static void setup() throws JSONException, org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        initialEntities = EntityMother.randomList(2);
        ScorpioInflator.addInitialEntitiesToContextBroker(brokerUrl, initialEntities);
    }

    @AfterAll
    static void tearDown() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        List<String> ids = initialEntities.stream().map(Entity::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);
    }

    @Test
    void itShouldReturnMissingExternalEntities() throws JsonProcessingException {
        DiscoverySyncRequest discoverySyncRequest = DiscoverySyncRequestMother.list1And2();
        Mono<DiscoverySyncRequest> discoverySyncRequestMono = Mono.just(discoverySyncRequest);
        String discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);

        DiscoverySyncResponse discoverySyncResponse = DiscoverySyncResponseMother.fromList(contextBrokerExternalDomain, initialEntities);
        String discoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        String response = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build()
                .post()
                .uri("/api/v1/sync/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .body(discoverySyncRequestMono, DiscoverySyncRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3).block();

        System.out.println("Request: " + discoverySyncRequestJson);
        System.out.println("Response: " + discoverySyncResponseJson);

        assertEquals(discoverySyncResponseJson, response);
    }

    void itShouldCreateLocalEntitiesBasedOnExternalEntities() {
        //TODO
        throw new NotImplementedException();
    }
}
