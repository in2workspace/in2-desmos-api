package es.in2.desmos.domain.services.broker.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScorpioAdapterIT {
    @Autowired
    private ScorpioAdapter scorpioAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    private final MediaType APPLICATION_LD_JSON = new MediaType("application", "ld+json");

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Test
    void itShouldReturnEntityIds() throws JSONException, JsonProcessingException {
        List<ProductOffering> initialEntities = createInitialEntities();
        addInitialEntitiesToContextBroker(contextBrokerExternalDomain, initialEntities);

        Mono<List<ProductOffering>> result = scorpioAdapter.getEntityIds();

        StepVerifier.create(result)
                .expectNext(initialEntities)
                .verifyComplete();
    }

    private void addInitialEntitiesToContextBroker(String brokerUrl, List<ProductOffering> initialEntities) throws JsonProcessingException, JSONException {
        String requestBody = createInitialEntitiesRequestBody(initialEntities);

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
    private String createInitialEntitiesRequestBody(List<ProductOffering> initialEntities) throws JsonProcessingException, JSONException {
        JSONArray productOfferingsJsonArray = new JSONArray();

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

    private @NotNull List<ProductOffering> createInitialEntities() {
        List<ProductOffering> initialEntities = new ArrayList<>();
        initialEntities.add(ProductOfferingMother.sample3());
        initialEntities.add(ProductOfferingMother.sample4());
        return initialEntities;
    }
}
