package es.in2.desmos.it.domain.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.MVBrokerEntity4DataNegotiation;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.it.ContainerManager;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import es.in2.desmos.objectmothers.MVBrokerEntity4DataNegotiationMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
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

    private static List<MVBrokerEntity4DataNegotiation> initialMvEntity4DataNegotiationList;

    @BeforeAll
    static void setUp() throws JSONException, JsonProcessingException {
        initialMvEntity4DataNegotiationList = createInitialMVEntity4DataNegotiation();
    }

    @AfterAll
    static void setDown() {
        removeInitialMVEntity4DataNegotiation();
    }

    @Test
    void itShouldReturnEntityIds() {
        String processId = "0";
        Mono<List<MVBrokerEntity4DataNegotiation>> result = scorpioAdapter.getMVBrokerEntities4DataNegotiation(processId, "ProductOffering", "lastUpdate", "version");

        StepVerifier.create(result)
                .expectNext(initialMvEntity4DataNegotiationList)
                .verifyComplete();
    }

    @Test
    void itShouldBatchUpsertEntities() {
        String processId = "0";
        String requestBody = EntitySyncResponseMother.sample;

        Mono<Void> result = scorpioAdapter.batchUpsertEntities(processId, requestBody);

        StepVerifier.create(result)
                .verifyComplete();

        Mono<String> entity1Mono = scorpioAdapter.getEntityById(processId, EntitySyncResponseMother.id1);
        StepVerifier
                .create(entity1Mono)
                .consumeNextWith(entity1 -> {
                    try {
                        JSONAssert.assertEquals(EntityMother.scorpioJson1(), entity1, true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();

        var entity2Mono = scorpioAdapter.getEntityById(processId, EntitySyncResponseMother.id2);
        StepVerifier
                .create(entity2Mono)
                .consumeNextWith(entity2 -> {
                    try {
                        JSONAssert.assertEquals(EntityMother.scorpioJson2(), entity2, true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();

        var entity3Mono = scorpioAdapter.getEntityById(processId, EntitySyncResponseMother.id3);
        StepVerifier
                .create(entity3Mono)
                .consumeNextWith(entity3 -> {
                    try {
                        JSONAssert.assertEquals(EntityMother.scorpioJson3(), entity3, true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();

        var entity4Mono = scorpioAdapter.getEntityById(processId, EntitySyncResponseMother.id4);
        StepVerifier
                .create(entity4Mono)
                .consumeNextWith(entity4 -> {
                    try {
                        JSONAssert.assertEquals(EntityMother.scorpioJson4(), entity4, true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }

    private static List<MVBrokerEntity4DataNegotiation> createInitialMVEntity4DataNegotiation() throws JSONException, JsonProcessingException {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        var entities = MVBrokerEntity4DataNegotiationMother.randomList(2);
        ScorpioInflator.addInitialEntitiesToContextBroker(brokerUrl, entities);
        return entities;
    }

    private static void removeInitialMVEntity4DataNegotiation() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();

        List<String> ids = MVEntity4DataNegotiationMother.fullList().stream().map(MVEntity4DataNegotiation::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);

        ids = initialMvEntity4DataNegotiationList.stream().map(MVBrokerEntity4DataNegotiation::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);

    }
}
