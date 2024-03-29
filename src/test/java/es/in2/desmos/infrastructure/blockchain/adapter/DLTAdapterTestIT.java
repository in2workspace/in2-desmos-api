package es.in2.desmos.infrastructure.blockchain.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.model.DLTEvent;
import es.in2.desmos.infrastructure.blockchain.model.DLTAdapterSubscription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;

import static es.in2.desmos.ContainerManager.getBaseUriBlockchainAdapter;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;


@SpringBootTest
@Testcontainers
class DLTAdapterTestIT {
    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Autowired
    private DigitelDLTAdapter dltAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.builder().baseUrl(getBaseUriBlockchainAdapter()).build();

    private final DLTEvent dltEvent = new DLTEvent(
            "ProductOffering",
            "0x983c5a1eb59ea6861c3e27b64dd3f1fd50233c3229149b8d139798a17b4cb0ec",
            "0xad340ef6baa0ad1dbdd8e24e2d71b2fe671c2bbe28ea8c6d4f2ce54f00b7ff6f",
            "0x0000000000000000000000000000000000000000000000000000000000000000",
            "http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:in2-1122?hl=645a33be4157f40e32d74ea0c6599a59d31f8d72c0f2fffda5438cce608b1762",
            List.of("ExampleMetadata1", "ExampleMetadata2")
    );

    private final DLTAdapterSubscription dltAdapterSubscription = new DLTAdapterSubscription(
            List.of("ProductOffering", "ProductOrder"),
            "http://blockchain-connector:8080/notifications/broker"
    );

    @Test
    void shouldPublishBlockchainEvent() {
        // Arrange
        String expectedResponse = """
{
  "eventType": "ProductOffering",
  "iss": "0x983c5a1eb59ea6861c3e27b64dd3f1fd50233c3229149b8d139798a17b4cb0ec",
  "entityId": "urn:ngsi-ld:product-offering:in2-1122",
  "previousEntityHash": "0x0000000000000000000000000000000000000000000000000000000000000000",
  "dataLocation": "http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:product-offering:in2-1122?hl=645a33be4157f40e32d74ea0c6599a59d31f8d72c0f2fffda5438cce608b1762",
  "metadata": ["ExampleMetadata1", "ExampleMetadata2"]
}
""";
        // Act
        dltAdapter.publishEvent("1234", dltEvent).block();
        // Assert
        String response = webClient
                .post()
                .uri("/api/v1/publishEvent")
                .exchangeToMono(clientResponse -> {
                    var body = clientResponse.bodyToMono(String.class);
                    System.out.println(clientResponse.statusCode());
                    System.out.println(body);
                    return body;
                })
                .block();
        assertNotNull(response);
    }







}
