package es.in2.desmos.domain.services.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.BrokerSubscription;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static es.in2.desmos.ContainerManager.getBaseUriForScorpioA;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrokerListenerServiceIT {

    private final ObjectMapper objectMapper = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES).build();

    @Autowired
    private BrokerAdapterFactory brokerAdapterFactory;

    private BrokerAdapterService brokerAdapterService;

    private WebClient webClient;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @BeforeEach
    void setUp() {
        brokerAdapterService = brokerAdapterFactory.getBrokerAdapter();
        // Ensure WebClient is set up with the correct base URL if it's not already configured in the setup method
        webClient = WebClient.builder().baseUrl(getBaseUriForScorpioA()).build();
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
        brokerAdapterService.createSubscription("08b0f80f-9098-47bf-a4f4-244210d532d5", brokerSubscription).block();
        // Assert
        String response = webClient.get()
                .uri("/ngsi-ld/v1/subscriptions/urn:subscription:b74a701a-9a3b-4eff-982e-744652fc2abd")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .block();
        BrokerSubscription brokerSubscriptionResponse = objectMapper.readValue(response, BrokerSubscription.class);
        System.out.println(brokerSubscriptionResponse.toString());
//        assertEquals(brokerSubscription.id(), brokerSubscriptionResponse.id());
    }

}
