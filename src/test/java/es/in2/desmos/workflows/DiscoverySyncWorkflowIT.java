package es.in2.desmos.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.objectmothers.DiscoverySyncRequestMother;
import es.in2.desmos.objectmothers.DiscoverySyncResponseMother;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

    private final MediaType APPLICATION_LD_JSON = new MediaType("application", "ld+json");

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Test
    void itShouldReturnMissingExternalEntities() throws JsonProcessingException, JSONException {
        DiscoverySyncRequest discoverySyncRequest = DiscoverySyncRequestMother.list1And2();
        Mono<DiscoverySyncRequest> discoverySyncRequestMono = Mono.just(discoverySyncRequest);
        String discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);

        DiscoverySyncResponse discoverySyncResponse = DiscoverySyncResponseMother.list3And4(contextBrokerExternalDomain);
        String discoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        addInitialEntitiesToContextBroker(contextBrokerExternalDomain);

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
        System.out.println("Response: " +  discoverySyncResponseJson);

        assertEquals(discoverySyncResponseJson, response);
    }

    @Test
    void itShouldCreateLocalEntitiesBasedOnExternalEntities(){
        //TODO
        throw new NotImplementedException();
    }

    private void addInitialEntitiesToContextBroker(String brokerUrl) throws JsonProcessingException, JSONException {
        String requestBody = createInitialEntitiesRequestBody();

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

    @NotNull
    private String createInitialEntitiesRequestBody() throws JsonProcessingException, JSONException {
        JSONArray productOfferingsJsonArray = new JSONArray();

        var initialEntities = ProductOfferingMother.list3And4();

        for (var productOffering : initialEntities) {
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
        return requestBody;
    }
}
