package es.in2.desmos.broker.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.broker.model.BrokerSubscription;
import es.in2.desmos.broker.service.BrokerPublicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static es.in2.desmos.ContainerManager.getBaseUriForScorpio;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class BrokerSubscriptionInitializerIT {

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Autowired
    private BrokerSubscriptionInitializer brokerSubscriptionInitializer;

    @Autowired
    private BrokerPublicationService brokerPublicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.builder().baseUrl(getBaseUriForScorpio()).build();

    private final String entityCreate = """
                {"@context":[{"isPartOf":"myuniqueuri:isPartOf","Room":"urn:mytypes:room","temperature":"myuniqueuri:temperature"},"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"],"id":"house2:smartrooms:room1","isPartOf":{"object":"smartcity:houses:house2","type":"Relationship"},"temperature":{"providedBy":{"object":"smartbuilding:house2:sensor0815","type":"Relationship"},"type":"Property","unitCode":"CEL","value":23},"type":"Room"}
                """;

    @Test
    void shouldInitializeBrokerSubscription() throws JsonProcessingException {
        // Arrange
        List<BrokerSubscription> brokerSubscriptionList = List.of(
                BrokerSubscription.builder()
                        .id("urn:ngsi-ld:Subscription:1234")
                        .type("Subscription")
                        .entities(List.of(
                                BrokerSubscription.Entity.builder().type("ProductOffering").build(),
                                BrokerSubscription.Entity.builder().type("ProductOrder").build()
                        ))
                        .notification(BrokerSubscription.SubscriptionNotification.builder()
                                .subscriptionEndpoint(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.builder()
                                        .uri("http://blockchain-connector:8080/notifications/broker")
                                        .accept("application/json")
                                        .receiverInfo(List.of(
                                                BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                                                        .contentType("application/json")
                                                        .build()
                                        ))
                                        .build())
                                .build())
                        .build()
        );
        // Act
        brokerSubscriptionInitializer.setBrokerEntitySubscription().block();
        // Assert
        String response = webClient
                .get()
                .uri("/ngsi-ld/v1/subscriptions")
                .exchangeToMono(clientResponse -> {
                    var body = clientResponse.bodyToMono(String.class);
                    System.out.println(clientResponse.statusCode());
                    System.out.println(body);
                    return body;
                })
                .block();
        Assertions.assertNotNull(response);
        BrokerSubscription[] subscriptions = objectMapper.readValue(response, BrokerSubscription[].class);
        Assertions.assertTrue(subscriptions.length > 0, "We need at least one subscription");
        assertEquals("Subscription", subscriptions[0].type());
        Assertions.assertTrue(subscriptions[0].entities().stream().anyMatch(e -> e.type().equals("ProductOffering")));
        Assertions.assertTrue(subscriptions[0].entities().stream().anyMatch(e -> e.type().equals("ProductOrder")));
    }

}
