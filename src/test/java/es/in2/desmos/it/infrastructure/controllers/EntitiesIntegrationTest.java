package es.in2.desmos.it.infrastructure.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.utils.Base64Converter;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.it.ContainerManager;
import es.in2.desmos.objectmothers.BrokerDataMother;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithMockUser
public class EntitiesIntegrationTest {

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @LocalServerPort
    private int localServerPort;

    @Autowired
    ObjectMapper objectMapper;

    private WebTestClient webTestClient;

    private static final List<String> brokerEntitiesIds = List.of(
            BrokerDataMother.GET_ENTITY_REQUEST_ENTITY_ID,
            BrokerDataMother.GET_ENTITY_REQUEST_SUBENTITY_1_ID,
            BrokerDataMother.GET_ENTITY_REQUEST_SUBENTITY_2_ID
    );

    private static final String brokerEntitiesJson =
            BrokerDataMother.GET_ENTITY_REQUEST_WITH_SUB_ENTITIES_ARRAY_JSON_VARIABLE;

    @BeforeAll
    static void setup() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        ScorpioInflator.addEntitiesToBroker(
                brokerUrl,
                brokerEntitiesJson);
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

        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, brokerEntitiesIds);
    }

    @Test
    void test() throws JSONException, JsonProcessingException {
        JSONArray expectedResponseJsonArray = new JSONArray(brokerEntitiesJson);
        List<String> expectedBrokerEntities = new ArrayList<>();
        for (int i = 0; i < expectedResponseJsonArray.length(); i++) {
            String entity = expectedResponseJsonArray.getString(i);
            expectedBrokerEntities.add(entity);
        }

        String expectedEntitiesJson = getJsonNodeFromStringsList(expectedBrokerEntities).toString();

        webTestClient
                .get()
                .uri("/api/v1/entities/{id}", BrokerDataMother.GET_ENTITY_REQUEST_ENTITY_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Entity.class)
                .consumeWith(response -> {
                    try {
                        List<Entity> entitiesResponse = response.getResponseBody();
                        String actualEntitiesJson = getJsonNodeFromEntitiesBase64List(
                                Objects.requireNonNull(entitiesResponse)).toString();

                        JSONAssert.assertEquals(expectedEntitiesJson, actualEntitiesJson, false);
                    } catch (JsonProcessingException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private JsonNode getJsonNodeFromEntitiesBase64List(List<Entity> localEntities) throws JsonProcessingException {
        List<String> stringList = localEntities
                .stream()
                .map(entity -> Base64Converter.convertBase64ToString(entity.value()))
                .toList();

        return getJsonNodeFromStringsList(stringList);
    }

    private JsonNode getJsonNodeFromStringsList(List<String> localEntities) throws JsonProcessingException {
        List<JsonObject> localEntitiesObjects = new ArrayList<>();
        for (var entity : localEntities) {
            localEntitiesObjects.add(JsonParser.parseString(entity).getAsJsonObject());
        }

        return objectMapper.readTree(localEntitiesObjects.toString());
    }
}
