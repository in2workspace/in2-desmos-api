package es.in2.desmos.it.domain.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.BrokerEntityWithIdTypeLastUpdateAndVersion;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.it.ContainerManager;
import es.in2.desmos.objectmothers.EntityMother;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private static BrokerEntityWithIdTypeLastUpdateAndVersion[] initialMvEntity4DataNegotiationList;

    @BeforeAll
    static void setUp() throws JSONException, JsonProcessingException {
        initialMvEntity4DataNegotiationList = createInitialMVEntity4DataNegotiation();
    }

    @AfterAll
    static void setDown() {
        removeInitialMVEntity4DataNegotiation();
    }

    private static BrokerEntityWithIdTypeLastUpdateAndVersion[] createInitialMVEntity4DataNegotiation() throws JSONException, JsonProcessingException {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        var entities = MVBrokerEntity4DataNegotiationMother.randomList(2);
        ScorpioInflator.addInitialEntitiesToContextBroker(brokerUrl, entities);
        return entities.toArray(BrokerEntityWithIdTypeLastUpdateAndVersion[]::new);
    }

    @Test
    void itShouldBatchUpsertEntities() throws JsonProcessingException {
        String processId = "0";
        String requestBody = EntityMother.getFullJsonList();

        Mono<Void> result = scorpioAdapter.batchUpsertEntities(processId, requestBody);

        StepVerifier.create(result)
                .verifyComplete();

        Mono<String> entity1Mono = scorpioAdapter.getEntityById(processId, MVEntity4DataNegotiationMother.sample1().id());
        StepVerifier
                .create(entity1Mono)
                .consumeNextWith(entity1 -> {
                    try {
                        JSONAssert.assertEquals(EntityMother.scorpioDefaultJson1(), entity1, true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();

        var entity2Mono = scorpioAdapter.getEntityById(processId, MVEntity4DataNegotiationMother.sample2().id());
        StepVerifier
                .create(entity2Mono)
                .consumeNextWith(entity2 -> {
                    try {
                        JSONAssert.assertEquals(EntityMother.scorpioDefaultJson2(), entity2, true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();

        var entity3Mono = scorpioAdapter.getEntityById(processId, MVEntity4DataNegotiationMother.sample3().id());
        StepVerifier
                .create(entity3Mono)
                .consumeNextWith(entity3 -> {
                    try {
                        JSONAssert.assertEquals(EntityMother.scorpioDefaultJson3(), entity3, true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();

        var entity4Mono = scorpioAdapter.getEntityById(processId, MVEntity4DataNegotiationMother.sample4().id());
        StepVerifier
                .create(entity4Mono)
                .consumeNextWith(entity4 -> {
                    try {
                        JSONAssert.assertEquals(EntityMother.scorpioDefaultJson4(), entity4, true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();

        scorpioAdapter.deleteEntityById("0", MVEntity4DataNegotiationMother.sampleScorpio1().id());
        scorpioAdapter.deleteEntityById("0", MVEntity4DataNegotiationMother.sampleScorpio2().id());
        scorpioAdapter.deleteEntityById("0", MVEntity4DataNegotiationMother.sampleScorpio3().id());
        scorpioAdapter.deleteEntityById("0", MVEntity4DataNegotiationMother.sampleScorpio4().id());
    }

    @Test
    void itShouldReturnEntityIds() {
        String processId = "0";
        Mono<BrokerEntityWithIdTypeLastUpdateAndVersion[]> resultMono = scorpioAdapter.findAllIdTypeFirstAttributeAndSecondAttributeByType(processId, "ProductOffering", "lastUpdate", "version", BrokerEntityWithIdTypeLastUpdateAndVersion[].class);

        StepVerifier.create(resultMono)
                .consumeNextWith(result -> assertEquals(Arrays.stream(initialMvEntity4DataNegotiationList).toList(), Arrays.stream(result).toList()))
                .verifyComplete();
    }

    private static void removeInitialMVEntity4DataNegotiation() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();

        List<String> ids = MVEntity4DataNegotiationMother.fullList().stream().map(MVEntity4DataNegotiation::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);

        ids = Arrays.stream(initialMvEntity4DataNegotiationList).toList().stream().map(BrokerEntityWithIdTypeLastUpdateAndVersion::getId).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);

    }
}
