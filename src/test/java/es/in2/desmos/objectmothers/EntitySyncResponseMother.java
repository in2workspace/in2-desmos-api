package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.EntitySyncResponse;
import org.jetbrains.annotations.NotNull;

public class EntitySyncResponseMother {
    private EntitySyncResponseMother() {
    }

    public static @NotNull EntitySyncResponse sample() {
        return new EntitySyncResponse(sampleJson);
    }

    private static final String sampleJson =
            """
                    [
                      {
                        "id": "urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0",
                        "type": "productOffering",
                        "productSpecification": {
                          "id": "spec-broadband-001",
                          "name": "1Gbps Broadband Spec"
                        },
                        "productOfferingPrice": {
                          "type": "Relationship",
                          "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                        }
                      },
                      {
                        "id": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a",
                        "type": "productOfferingPrice",
                        "name": "Monthly Subscription Fee",
                        "priceType": "recurring",
                        "price": {
                          "amount": "49.99",
                          "currency": "USD"
                        },
                        "recurringChargePeriod": "monthly"
                      }
                    ]
                    """;

}
