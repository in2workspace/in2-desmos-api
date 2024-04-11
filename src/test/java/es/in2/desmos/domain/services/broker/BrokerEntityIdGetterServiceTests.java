package es.in2.desmos.domain.services.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.List;

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

    private static List<ProductOffering> initialEntities;

    private final MediaType APPLICATION_LD_JSON = new MediaType("application", "ld+json");

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @BeforeAll
    static void setup() throws JSONException, JsonProcessingException, org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        initialEntities = ProductOfferingMother.randomList(2);
        ScorpioInflator.addInitialEntitiesToContextBroker(brokerUrl, initialEntities);
    }

    @AfterAll
    static void tearDown(){
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        List<String> ids = initialEntities.stream().map(ProductOffering::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);
    }


    @Test
    void itShouldReturnEntityIds() throws JSONException, JsonProcessingException {
        var result = brokerEntityIdGetterService.getData();

        StepVerifier.create(result)
                .expectNext(initialEntities)
                .verifyComplete();
    }
}
