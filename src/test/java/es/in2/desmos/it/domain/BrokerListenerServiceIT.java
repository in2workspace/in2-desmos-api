package es.in2.desmos.it.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import es.in2.desmos.domain.models.BrokerSubscription;
import es.in2.desmos.domain.services.broker.adapter.ScorpioAdapter;
import es.in2.desmos.it.ContainerManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static es.in2.desmos.it.ContainerManager.getBaseUriForScorpioA;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrokerListenerServiceIT {

    private final ObjectMapper objectMapper = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES).build();

    @Autowired
    private ScorpioAdapter scorpioAdapter;

    private final WebClient webClient = WebClient.builder().baseUrl(getBaseUriForScorpioA()).build();

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    // Create a subscription to the Context Broker
    @Order(1)
    @Test
    void shouldCreateSubscription() throws JsonProcessingException {
        // Arrange
        BrokerSubscription brokerSubscription = BrokerSubscription.builder()
                .id("urn:subscription:b74a701a-9a3b-4eff-982e-744652fc2abd")
                .type("Subscription")
                .entities(List.of(
                        BrokerSubscription.Entity.builder().type("ProductOffering").build(),
                        BrokerSubscription.Entity.builder().type("Category").build(),
                        BrokerSubscription.Entity.builder().type("Catalogue").build()))
                .notification(BrokerSubscription.SubscriptionNotification.builder()
                        .subscriptionEndpoint(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.builder()
                                .uri("http://localhost:8080/api/v1/notifications/broker")
                                .accept("application/json")
                                .receiverInfo(List.of(
                                        BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                                                .contentType("application/json")
                                                .build()))
                                .build())
                        .build())
                .build();
        // Act
        scorpioAdapter.createSubscription("08b0f80f-9098-47bf-a4f4-244210d532d5", brokerSubscription).block();
        // Assert
        String response = webClient.get()
                .uri("/ngsi-ld/v1/subscriptions/urn:subscription:b74a701a-9a3b-4eff-982e-744652fc2abd")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .block();
        BrokerSubscription brokerSubscriptionResponse = objectMapper.readValue(response, BrokerSubscription.class);
        Assertions.assertEquals(brokerSubscription, brokerSubscriptionResponse);
    }

}
