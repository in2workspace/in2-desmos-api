package es.in2.desmos.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.objectmothers.DiscoverySyncRequestMother;
import es.in2.desmos.objectmothers.DiscoverySyncResponseMother;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DiscoverySyncWorkflowIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    private final MediaType APPLICATION_LD_JSON = new MediaType("application", "ld+json");

    @Test
    void itShouldCreateLocalEntitiesBasedOnExternalEntitiesAndReturnMissingExternalEntities() throws JsonProcessingException, JSONException {
        ObjectMapper objectMapper = new ObjectMapper();

        var discoverySyncRequest = DiscoverySyncRequestMother.simpleDiscoverySyncRequest();
        var discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);

        var discoverySyncResponse = DiscoverySyncResponseMother.fullDiscoverySyncResponse(contextBrokerExternalDomain);
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
    }

    private void AddInitialEntitiesToContextBroker(String brokerUrl) throws JsonProcessingException, JSONException {
        JSONArray productOfferingsJsonArray = new JSONArray();

        for (var productOffering : CreateInitialEntities()) {
            var productOfferingJsonText = objectMapper.writeValueAsString(productOffering);
            var productOfferingJson = new JSONObject(productOfferingJsonText);

            productOfferingJson.put("type", "ProductOffering");

            var contextValueFakeList = new JSONArray();
            contextValueFakeList.put("https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld");
            productOfferingJson.put("@context", contextValueFakeList);

            productOfferingsJsonArray.put(productOfferingJson);
        }

        String requestBody = productOfferingsJsonArray.toString();
        requestBody = requestBody.replace("\\/", "/");

        WebClient.builder()
                .baseUrl(brokerUrl)
                .build()
                .post()
                .uri("ngsi-ld/v1/entityOperations/create")
                .contentType(APPLICATION_LD_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .retry(3).block();
    }

    private List<ProductOffering> CreateInitialEntities() {
        List<ProductOffering> initialEntities = new ArrayList<>();
        initialEntities.add(ProductOfferingMother.sample3());
        initialEntities.add(ProductOfferingMother.sample4());
        return initialEntities;
    }
}
