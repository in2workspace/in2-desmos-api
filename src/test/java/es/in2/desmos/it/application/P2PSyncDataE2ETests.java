package es.in2.desmos.it.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.it.ContainerManager;
import es.in2.desmos.objectmothers.AuditRecordMother;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.port=55555"})
class P2PSyncDataE2ETests {

    public static final String LOCAL_ISSUER = "http://localhost:55555";

    @Autowired
    private AuditRecordService auditRecordService;

    @Autowired
    private ScorpioAdapter scorpioAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int localServerPort;


    private List<MVEntity4DataNegotiation> initialMvEntity4DataNegotiationList;

    @DynamicPropertySource
    private static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
        ContainerManager.externalAccessNodesProperties(registry);

        registry.add("operator.externalDomain", () -> LOCAL_ISSUER);
    }

    @BeforeEach
    void setUp() throws IOException {
        initialMvEntity4DataNegotiationList = createInitialEntitiesInScorpio(
                ContainerManager.getBaseUriForScorpioA(),
                EntityMother.getJsonList1And2OldAnd3(),
                List.of(
                        MVEntity4DataNegotiationMother.sampleScorpio1(),
                        MVEntity4DataNegotiationMother.sampleBase2Old(),
                        MVEntity4DataNegotiationMother.sampleScorpio3()));

        createInitialEntitiesInScorpio(
                ContainerManager.getBaseUriForScorpioB(),
                EntityMother.getListJson2And4(), List.of());

        createInitialEntitiesInAuditRecord(auditRecordService, initialMvEntity4DataNegotiationList);
    }

    @AfterEach
    void setDown() {
        removeInitialEntitiesInScorpio();
        removeInitialEntitiesInAuditRecord(auditRecordService, initialMvEntity4DataNegotiationList);
    }

    @Test
    void itShould() throws JSONException {
        String desmosBUri = ContainerManager.getBaseUriDesmosB();
        System.out.println("El B: " + desmosBUri);

        Mono<Void> response = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build()
                .get()
                .uri("/api/v1/sync/p2p/data")
                .retrieve()
                .bodyToMono(Void.class)
                .retry(3);

        StepVerifier
                .create(response)
                .verifyComplete();

        assertScorpioAEntityIsExpected(MVEntity4DataNegotiationMother.sampleScorpio1().id(), EntityMother.scorpioJson1());
        assertScorpioAEntityIsExpected(MVEntity4DataNegotiationMother.sampleScorpio2().id(), EntityMother.scorpioJson2());
        assertScorpioAEntityIsExpected(MVEntity4DataNegotiationMother.sampleScorpio3().id(), EntityMother.scorpioJson3());
        assertScorpioAEntityIsExpected(MVEntity4DataNegotiationMother.sampleScorpio4().id(), EntityMother.scorpioJson4());

        String externalDomain = ContainerManager.getBaseUriDesmosB();
        assertAuditRecordAEntityIsExpected(MVEntity4DataNegotiationMother.sampleScorpio1().id(), MVEntity4DataNegotiationMother.sampleScorpio1(), LOCAL_ISSUER);
        assertAuditRecordAEntityIsExpected(MVEntity4DataNegotiationMother.sampleScorpio2().id(), MVEntity4DataNegotiationMother.sampleBase2(), externalDomain);
        assertAuditRecordAEntityIsExpected(MVEntity4DataNegotiationMother.sampleScorpio3().id(), MVEntity4DataNegotiationMother.sampleScorpio3(), LOCAL_ISSUER);
        assertAuditRecordAEntityIsExpected(MVEntity4DataNegotiationMother.sampleScorpio4().id(), MVEntity4DataNegotiationMother.sampleBase4(), externalDomain);
    }

    private void assertScorpioAEntityIsExpected(String entityId, String expectedEntityResponse) {
        await().atMost(5, TimeUnit.SECONDS).ignoreExceptions().until(() -> {
            String processId = "0";
            Mono<String> entityresponseMono = scorpioAdapter.getEntityById(processId, entityId);
            StepVerifier
                    .create(entityresponseMono)
                    .consumeNextWith(entityResponse -> {
                        try {
                            System.out.println("Entity response for Scorpio entity check: " + entityResponse);
                            JSONAssert.assertEquals(expectedEntityResponse, entityResponse, false);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .expectComplete()
                    .verify();

            return true;
        });
    }

    private void assertAuditRecordAEntityIsExpected(String entityId, MVEntity4DataNegotiation expectedMVEntity4DataNegotiation, String baseUri) {
        await().atMost(10, TimeUnit.SECONDS).until(() -> {
            String processId = "0";
            Mono<AuditRecord> auditRecordMono = auditRecordService.findLatestAuditRecordForEntity(processId, entityId);

            StepVerifier
                    .create(auditRecordMono)
                    .consumeNextWith(auditRecord -> {
                        System.out.println("Entity audit record AuditRecord entity check: " + auditRecord);

                        AuditRecord expectedAuditRecord = AuditRecordMother.createAuditRecordFromMVEntity4DataNegotiation(baseUri, expectedMVEntity4DataNegotiation, AuditRecordStatus.PUBLISHED);

                        assertThat(auditRecord)
                                .usingRecursiveComparison()
                                .ignoringFields("processId", "id", "createdAt", "hash", "hashLink", "newTransaction")
                                .isEqualTo(expectedAuditRecord);
                    })
                    .expectComplete()
                    .verify();

            return true;
        });
    }

    private @NotNull List<MVEntity4DataNegotiation> createInitialEntitiesInScorpio(String scorpioUri, String responseEntities, @NotNull List<MVEntity4DataNegotiation> mvEntity4DataNegotiations) {
        ScorpioInflator.addInitialEntitiesToContextBroker(scorpioUri, responseEntities);

        return mvEntity4DataNegotiations;
    }

    private void removeInitialEntitiesInScorpio() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        List<String> ids = MVEntity4DataNegotiationMother.fullList().stream().map(MVEntity4DataNegotiation::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);
    }

    private void createInitialEntitiesInAuditRecord(AuditRecordService auditRecordService, List<MVEntity4DataNegotiation> entities) {
        auditRecordService.fetchMostRecentAuditRecord().block();

        String processId = "0";
        for (var entity : entities) {
            auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, LOCAL_ISSUER, entity, AuditRecordStatus.PUBLISHED).block();
            AuditRecord auditRecord = auditRecordService.findLatestAuditRecordForEntity(processId, entity.id()).block();
            System.out.println("Published audit record: " + auditRecord);
        }
    }

    private void removeInitialEntitiesInAuditRecord(AuditRecordService auditRecordService, @NotNull List<MVEntity4DataNegotiation> entities) {
        String processId = "0";
        String issuer = "http://example.org";
        for (var entity : entities) {
            auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, entity, AuditRecordStatus.DELETED).block();
        }
    }
}
