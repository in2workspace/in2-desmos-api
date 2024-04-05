package es.in2.desmos.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// @SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DiscoverySyncWorkflowIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final MediaType APPLICATION_LD_JSON = new MediaType("application", "ld+json");

    @Test
    void itShouldCreateLocalEntitiesBasedOnExternalEntitiesAndReturnMissingExternalEntities() throws JsonProcessingException {
        /*ObjectMapper objectMapper = new ObjectMapper();

        var discoverySyncRequest = DiscoverySyncRequestMother.simpleDiscoverySyncRequest();
        var discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);

        var discoverySyncResponse = DiscoverySyncResponseMother.fullDiscoverySyncResponse();
        var discoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);*/

        var brokerUrl = ContainerManager.getBaseUriForScorpioA();
        AddInitialEntitiesToContextBroker(brokerUrl);


        /*webTestClient.post()
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
                .consumeWith(System.out::println);*/
    }

    private void AddInitialEntitiesToContextBroker(String brokerUrl) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // var requestBody = objectMapper.writeValueAsString(CreateInitialEntities());
        // var requestBody = objectMapper.writeValueAsString(ProductOfferingMother.sample1());
        Map<String, Object> productOfferingMap = objectMapper.convertValue(ProductOfferingMother.sample1(), Map.class);
        productOfferingMap.put("type", "ProductOffering");
        var contextValueFakeList = new ArrayList<String>();
        contextValueFakeList.add("\"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld\"");
        productOfferingMap.put("@context", contextValueFakeList);
        var requestBody = objectMapper.writeValueAsString(productOfferingMap);
        requestBody = requestBody.replace("\\\"", "");
        // var requestBody = example();

        var request = WebClient.builder()
                .baseUrl(brokerUrl)
                .build()
                .post()
                .uri("ngsi-ld/v1/entities/")
                .contentType(APPLICATION_LD_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .retry(3).block();

        String response = WebClient.builder()
                .baseUrl(brokerUrl)
                .build()
                .get()
                .uri("ngsi-ld/v1/entities/urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb")
                .accept(APPLICATION_LD_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3)
                .block();

        System.out.println(request);
        System.out.println(response);
        int c = 3;
    }

    private List<ProductOffering> CreateInitialEntities() {
        List<ProductOffering> initialEntities = new ArrayList<>();
        initialEntities.add(ProductOfferingMother.sample3());
        initialEntities.add(ProductOfferingMother.sample4());
        return initialEntities;
    }

    private String example() {
        return """
                  {
                  "id": "house2:smartrooms:room1",
                  "type": "Room",
                  "@context": ["https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"]
                }""";
    }
}
