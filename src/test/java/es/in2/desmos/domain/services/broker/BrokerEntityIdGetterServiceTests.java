package es.in2.desmos.domain.services.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.objectmothers.ProductOfferingMother;
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
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrokerEntityIdGetterServiceTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    BrokerEntityIdGetterService brokerEntityIdGetterService;

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
    void itShouldReturnEntityIds() throws JSONException, JsonProcessingException {
        addInitialEntitiesToContextBroker(contextBrokerExternalDomain);

        var expectedProductOffer = ProductOfferingMother.list3And4();

        var result = brokerEntityIdGetterService.getData();

        StepVerifier.create(result)
                .expectNext(expectedProductOffer)
                .verifyComplete();
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
