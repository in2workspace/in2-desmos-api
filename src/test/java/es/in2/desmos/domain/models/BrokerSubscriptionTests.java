package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrokerSubscriptionTests {

    private final String id = "urn:ngsi-ld:Subscription:185d05ee-3f9b-4d40-8b1b-effffa693fb1";
    private final String type = "Subscription";
    private final List<BrokerSubscription.Entity> entities =
            List.of(new BrokerSubscription.Entity("ProductOffering"),
                    new BrokerSubscription.Entity("Category"),
                    new BrokerSubscription.Entity("Catalogue"));
    private final BrokerSubscription.SubscriptionNotification notification = BrokerSubscription.SubscriptionNotification.builder()
            .subscriptionEndpoint(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.builder()
                    .uri("http://localhost:8080/ngsi-ld/v1/subscription")
                    .accept("application/json")
                    .receiverInfo(List.of(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                            .contentType("application/json")
                            .build()))
                    .build())
            .build();

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        BrokerSubscription brokerSubscription = BrokerSubscription.builder()
                .id(id)
                .type(type)
                .entities(entities)
                .notification(notification)
                .build();
        assertEquals(id, brokerSubscription.id());
        assertEquals(type, brokerSubscription.type());
        assertEquals(entities, brokerSubscription.entities());
        assertEquals(notification, brokerSubscription.notification());
    }

    @Test
    void testToString() {
        // Arrange
        BrokerSubscription brokerSubscription = BrokerSubscription.builder()
                .id(id)
                .type(type)
                .entities(entities)
                .notification(notification)
                .build();
        // Act
        String result = brokerSubscription.toString();
        // Assert
        assertTrue(result.contains(id));
        assertTrue(result.contains(type));
        assertTrue(result.contains(entities.toString()));
        assertTrue(result.contains(notification.toString()));
    }

    @Test
    void testBlockchainTxPayloadBuilderToString() {
        // Arrange
        String expectedToString = "BrokerSubscription.BrokerSubscriptionBuilder(id=" + id
                + ", type=" + type
                + ", entities=" + entities
                + ", notification=" + notification + ")";
        // Act
        BrokerSubscription.BrokerSubscriptionBuilder brokerSubscriptionBuilder = BrokerSubscription.builder()
                .id(id)
                .type(type)
                .entities(entities)
                .notification(notification);
        // Assert
        assertEquals(expectedToString, brokerSubscriptionBuilder.toString());
    }

    @Test
    void testBrokerSubscriptionEntityEntityBuilderAndLombokGeneratedMethods() {
        BrokerSubscription.Entity entity = BrokerSubscription.Entity.builder()
                .type("ProductOffering")
                .build();
        assertEquals("ProductOffering", entity.type());
    }

    @Test
    void testBrokerSubscriptionEntityEntityBuilderToString() {
        // Arrange
        String expectedToString = "BrokerSubscription.Entity.EntityBuilder(type=ProductOffering)";
        // Act
        BrokerSubscription.Entity.EntityBuilder entityBuilder = BrokerSubscription.Entity.builder()
                .type("ProductOffering");
        // Assert
        assertEquals(expectedToString, entityBuilder.toString());
    }

    @Test
    void BrokerSubscriptionSubscriptionNotificationSubscriptionEndpointRetrievalInfoContentTypeRetrievalInfoContentTypeBuilderToString() {
        // Arrange
        String expectedToString = "BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.RetrievalInfoContentTypeBuilder(contentType=application/json)";
        // Act
        BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.RetrievalInfoContentTypeBuilder retrievalInfoContentTypeBuilder = BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                .contentType("application/json");
        // Assert
        assertEquals(expectedToString, retrievalInfoContentTypeBuilder.toString());
    }

    @Test
    void BrokerSubscriptionSubscriptionNotificationSubscriptionNotificationBuilderToString() {
        // Arrange
        String expectedToString = "BrokerSubscription.SubscriptionNotification.SubscriptionNotificationBuilder(" +
                "subscriptionEndpoint=SubscriptionEndpoint[" +
                "uri=http://localhost:8080/ngsi-ld/v1/subscription, " +
                "accept=application/json, " +
                "receiverInfo=[RetrievalInfoContentType[contentType=application/json]]])";
        // Act
        BrokerSubscription.SubscriptionNotification.SubscriptionNotificationBuilder subscriptionNotificationBuilder = BrokerSubscription.SubscriptionNotification.builder()
                .subscriptionEndpoint(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.builder()
                        .uri("http://localhost:8080/ngsi-ld/v1/subscription")
                        .accept("application/json")
                        .receiverInfo(List.of(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                                .contentType("application/json")
                                .build()))
                        .build());
        // Assert
        assertEquals(expectedToString, subscriptionNotificationBuilder.toString());
    }

    @Test
    void BrokerSubscriptionSubscriptionNotificationSubscriptionEndpointSubscriptionEndpointBuilderToString() {
        // Arrange
        String expectedToString = "BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.SubscriptionEndpointBuilder(uri=http://localhost:8080/ngsi-ld/v1/subscription, accept=application/json, receiverInfo=[RetrievalInfoContentType[contentType=application/json]])";
        // Act
        BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.SubscriptionEndpointBuilder subscriptionEndpointBuilder = BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.builder()
                .uri("http://localhost:8080/ngsi-ld/v1/subscription")
                .accept("application/json")
                .receiverInfo(List.of(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                        .contentType("application/json")
                        .build()));
        // Assert
        assertEquals(expectedToString, subscriptionEndpointBuilder.toString());
    }

}
