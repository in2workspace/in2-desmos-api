package es.in2.desmos.infrastructure.broker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.z.services.BrokerPublicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;

import static es.in2.desmos.ContainerManager.getBaseUriForScorpio;

@SpringBootTest
@Testcontainers
class BrokerPublicationServiceIT {

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Autowired
    private BrokerPublicationService brokerPublicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.builder().baseUrl(getBaseUriForScorpio()).build();

    private final String entityCreate = """
                {"@context":[{"isPartOf":"myuniqueuri:isPartOf","Room":"urn:mytypes:room","temperature":"myuniqueuri:temperature"},"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"],"id":"house2:smartrooms:room1","isPartOf":{"object":"smartcity:houses:house2","type":"Relationship"},"temperature":{"providedBy":{"object":"smartbuilding:house2:sensor0815","type":"Relationship"},"type":"Property","unitCode":"CEL","value":23},"type":"Room"}
                """;

    @Test
    void shouldPublishEntity() throws JsonProcessingException {
        // Arrange
        String expectedResponse = """
                {"id":"house2:smartrooms:room1","myuniqueuri:isPartOf":{"object":"smartcity:houses:house2","type":"Relationship"},"myuniqueuri:temperature":{"providedBy":{"object":"smartbuilding:house2:sensor0815","type":"Relationship"},"type":"Property","unitCode":"CEL","value":23},"type":"urn:mytypes:room"}
                """;
        // Act
        brokerPublicationService.postEntity("1234", entityCreate).block();
        // Assert
        String response = webClient
                .get()
                .uri("/ngsi-ld/v1/entities/house2:smartrooms:room1")
                .exchangeToMono(clientResponse -> {
                    var body = clientResponse.bodyToMono(String.class);
                    System.out.println(clientResponse.statusCode());
                    System.out.println(body);
                    return body;
                })
                .block();
        Assertions.assertNotNull(response);
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        HashMap<String, Object> expectedMap = objectMapper.readValue(expectedResponse, typeRef);
        HashMap<String, Object> actualMap = objectMapper.readValue(response, typeRef);
        Assertions.assertEquals(expectedMap, actualMap);
    }

    // Update entity
    @Order(3)
    @Test
    void shouldUpdateEntity() throws JsonProcessingException {
        // Arrange
        String requestBody = """
                {"@context":[{"isPartOf":"myuniqueuri:isPartOf","Room":"urn:mytypes:room","temperature":"myuniqueuri:temperature"},"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"],"id":"house2:smartrooms:room1","isPartOf":{"object":"smartcity:houses:house2","type":"Relationship"},"temperature":{"providedBy":{"object":"smartbuilding:house2:sensor0815","type":"Relationship"},"type":"Property","unitCode":"CEL","value":25},"type":"Room"}
                """;
        String expectedResponse = """
                {"id":"house2:smartrooms:room1","myuniqueuri:isPartOf":{"object":"smartcity:houses:house2","type":"Relationship"},"myuniqueuri:temperature":{"providedBy":{"object":"smartbuilding:house2:sensor0815","type":"Relationship"},"type":"Property","unitCode":"CEL","value":25},"type":"urn:mytypes:room"}
                """;
        // Act
        brokerPublicationService.updateEntity("1234", requestBody).block();
        // Assert
        String response =webClient
                .get()
                .uri("/ngsi-ld/v1/entities/house2:smartrooms:room1")
                .exchangeToMono(clientResponse -> {
                    var body = clientResponse.bodyToMono(String.class);
                    System.out.println(clientResponse.statusCode());
                    System.out.println(body);
                    return body;
                })
                .block();
        Assertions.assertNotNull(response);
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        HashMap<String, Object> expectedMap = objectMapper.readValue(expectedResponse, typeRef);
        HashMap<String, Object> actualMap = objectMapper.readValue(response, typeRef);
        HashMap<String, Object> beforeUpdateMap = objectMapper.readValue(entityCreate, typeRef);

        Assertions.assertEquals(expectedMap, actualMap);
        Assertions.assertNotEquals(beforeUpdateMap, actualMap);
    }

    // Delete entity
    @Order(4)
    @Test
    void shouldDeleteEntity() throws JsonProcessingException {
        // Arrange
        String expectedResponse = """
                {"type":"https://uri.etsi.org/ngsi-ld/errors/ResourceNotFound","title":"Resource not found.","detail":"house2:smartrooms:room1 was not found","status":404}
                """;
        // Act
        brokerPublicationService.deleteEntityById("1234", "house2:smartrooms:room1").block();
        // Assert
        String response = webClient
                .get()
                .uri("/ngsi-ld/v1/entities/house2:smartrooms:room1")
                .exchangeToMono(clientResponse -> {
                    var body = clientResponse.bodyToMono(String.class);
                    System.out.println(clientResponse.statusCode());
                    System.out.println(body);
                    return body;
                })
                .block();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        HashMap<String, Object> expectedMap = objectMapper.readValue(expectedResponse, typeRef);
        HashMap<String, Object> actualMap = objectMapper.readValue(response, typeRef);
        Assertions.assertEquals(expectedMap, actualMap);
    }

}
