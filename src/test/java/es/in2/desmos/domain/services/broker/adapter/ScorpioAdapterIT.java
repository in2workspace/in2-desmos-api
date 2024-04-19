package es.in2.desmos.domain.services.broker.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
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

    private static List<MVEntity4DataNegotiation> initialMvEntity4DataNegotiationList;

    @BeforeAll
    static void setUp() throws JSONException, JsonProcessingException {
        createInitialEntities();
        initialMvEntity4DataNegotiationList = createInitialMVEntity4DataNegotiation();
    }

    @AfterAll
    static void setDown() {
        removeInitialEntities();
        removeInitialMVEntity4DataNegotiation(initialMvEntity4DataNegotiationList);
    }

    @Test
    void itShouldReturnEntityIds() {
        Mono<List<MVEntity4DataNegotiation>> result = scorpioAdapter.getMvEntities4DataNegotiation();

        StepVerifier.create(result)
                .expectNext(initialMvEntity4DataNegotiationList)
                .verifyComplete();
    }

    @Test
    void itShouldUpsertBatchEntities() {
        String processId = "0";
        String requestBody = EntitySyncResponseMother.sample();

        Mono<Void> result = scorpioAdapter.upsertBatchEntities(processId, requestBody);

        StepVerifier.create(result)
                .verifyComplete();

        var entity1 = scorpioAdapter.getEntityById(processId, EntitySyncResponseMother.getId1());
        StepVerifier
                .create(entity1)
                .expectNext(EntityMother.json1())
                .verifyComplete();

        var entity2 = scorpioAdapter.getEntityById(processId, EntitySyncResponseMother.getId2());
        StepVerifier
                .create(entity2)
                .expectNext(EntityMother.json2())
                .verifyComplete();

        var entity3 = scorpioAdapter.getEntityById(processId, EntitySyncResponseMother.getId3());
        StepVerifier
                .create(entity3)
                .expectNext(EntityMother.json3())
                .verifyComplete();

        var entity4 = scorpioAdapter.getEntityById(processId, EntitySyncResponseMother.getId4());
        StepVerifier
                .create(entity4)
                .expectNext(EntityMother.json4())
                .verifyComplete();
    }

    private static List<MVEntity4DataNegotiation> createInitialMVEntity4DataNegotiation() throws JSONException, JsonProcessingException {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        var entities = MVEntity4DataNegotiationMother.randomList(2);
        ScorpioInflator.addInitialEntitiesToContextBroker(brokerUrl, entities);
        return entities;
    }

    private static void removeInitialMVEntity4DataNegotiation(List<MVEntity4DataNegotiation> entities) {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        List<String> ids = entities.stream().map(MVEntity4DataNegotiation::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);
    }

    private static void createInitialEntities() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        String entities = EntitySyncResponseMother.sample1and2Old();
        ScorpioInflator.addInitialEntitiesToContextBroker(brokerUrl, entities);
    }

    private static void removeInitialEntities() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        List<String> ids = new ArrayList<>();
        ids.add(EntitySyncResponseMother.getId1());
        ids.add(EntitySyncResponseMother.getId2());
        ids.add(EntitySyncResponseMother.getId3());
        ids.add(EntitySyncResponseMother.getId4());
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);
    }
}
