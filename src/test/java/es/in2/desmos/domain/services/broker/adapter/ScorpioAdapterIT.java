package es.in2.desmos.domain.services.broker.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.objectmothers.EntityMother;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    private static List<Entity> initialEntities;

    @BeforeAll
    static void setup() throws JSONException, org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        initialEntities = EntityMother.randomList(2);
        ScorpioInflator.addInitialEntitiesToContextBroker(brokerUrl, initialEntities);
    }

    @AfterAll
    static void tearDown(){
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        List<String> ids = initialEntities.stream().map(Entity::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);
    }

    @Test
    void itShouldReturnEntityIds() {
        Mono<List<Entity>> result = scorpioAdapter.getEntityIds();

        StepVerifier.create(result)
                .expectNext(initialEntities)
                .verifyComplete();
    }


}
