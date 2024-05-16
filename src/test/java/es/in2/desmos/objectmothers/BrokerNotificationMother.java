package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.BrokerNotification;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class BrokerNotificationMother {
    private BrokerNotificationMother() {
    }

    public static BrokerNotification withDataArray() {
        Map<String, Object> productOffering1 = Map.of(
                "id", "urn:ngsi-ld:ProductOffering:122355255",
                "type", "ProductOffering",
                "description", Map.of("type", "Property", "value", "Example of a Product offering for cloud services suite"),
                "notifiedAt", "2024-04-10T11:33:43.807000Z"
        );

        Map<String, Object> productOffering2 = Map.of(
                "id", "urn:ngsi-ld:ProductOffering:552553221",
                "type", "ProductOffering",
                "description", Map.of("type", "Property", "value", "Another example of a Product offering for cloud services suite"), // Cambio en la descripción
                "notifiedAt", "2024-04-10T11:33:43.807000Z"
        );

        return BrokerNotification.builder()
                .id("notification:-5106976853901020699")
                .type("Notification")
                .data(List.of(productOffering1, productOffering2))
                .subscriptionId("urn:ngsi-ld:Subscription:122355255")
                .notifiedAt(Instant.now().toString())
                .build();
    }
}
