package es.in2.desmos.it.infrastructure.controllers;

import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.it.ContainerManager;
import es.in2.desmos.objectmothers.BrokerDataMother;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithMockUser
public class EntitiesIntegrationTest {

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    private WebTestClient webTestClient;

    private static final List<String> entitiesIds = List.of(
            BrokerDataMother.GET_ENTITY_REQUEST_ENTITY_ID,
            BrokerDataMother.GET_ENTITY_REQUEST_SUBENTITY_1_ID,
            BrokerDataMother.GET_ENTITY_REQUEST_SUBENTITY_2_ID
    );

    @LocalServerPort
    private int localServerPort;

    @BeforeAll
    static void setup() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        ScorpioInflator.addEntitiesToBroker(
                brokerUrl,
                BrokerDataMother.GET_ENTITY_REQUEST_WITH_SUB_ENTITIES_ARRAY_JSON_VARIABLE);
    }

    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + localServerPort)
                .build();
    }

    @AfterAll
    static void tearDown() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();

        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, entitiesIds);
    }

    @Test
    void test() {
        webTestClient
                .get()
                .uri("/api/v1/entities/{id}", BrokerDataMother.GET_ENTITY_REQUEST_ENTITY_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Entity.class)
                .consumeWith(response -> {
                    List<Entity> entities = response.getResponseBody();
                    int actualEntitiesSize = Objects.requireNonNull(entities).size();
                    int expectedEntitiesSize = entitiesIds.size();

                    assertThat(actualEntitiesSize).isEqualTo(expectedEntitiesSize);
                });
    }
}
