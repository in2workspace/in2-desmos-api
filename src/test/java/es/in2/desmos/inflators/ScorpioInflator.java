package es.in2.desmos.inflators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public final class ScorpioInflator {

    private ScorpioInflator() {
    }

    private final static MediaType APPLICATION_LD_JSON = new MediaType("application", "ld+json");

    public static void addInitialEntitiesToContextBroker(String brokerUrl, List<MVBrokerEntity4DataNegotiation> initialEntities) throws JSONException, JsonProcessingException {
        String requestBody = createInitialEntitiesRequestBody(initialEntities);

        var result = WebClient.builder()
                .baseUrl(brokerUrl)
                .build()
                .post()
                .uri("ngsi-ld/v1/entityOperations/create")
                .contentType(APPLICATION_LD_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3).block();

        System.out.println("Create entities to Scorpio: " + result);
    }

    public static void addInitialEntitiesToContextBroker(String brokerUrl, String requestBody) {
        var result = WebClient.builder()
                .baseUrl(brokerUrl)
                .build()
                .post()
                .uri("ngsi-ld/v1/entityOperations/create")
                .contentType(APPLICATION_LD_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3).block();

        System.out.println("Create entities to Scorpio: " + result);
    }

    public static void deleteInitialEntitiesFromContextBroker(String brokerUrl, List<String> ids){
        WebClient.builder()
                .baseUrl(brokerUrl)
                .build()
                .post()
                .uri("ngsi-ld/v1/entityOperations/delete")
                .contentType(APPLICATION_LD_JSON)
                .bodyValue(ids)
                .retrieve()
                .bodyToMono(Void.class)
                .retry(3).block();

        System.out.println("Remove entities from Scorpio.");
    }

    @NotNull
    private static String createInitialEntitiesRequestBody(List<MVBrokerEntity4DataNegotiation> initialEntities) throws JsonProcessingException, JSONException {
        ObjectMapper objectMapper = new ObjectMapper();
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
}
