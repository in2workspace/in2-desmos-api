package es.in2.desmos.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.objectmothers.*;
import es.in2.desmos.workflows.jobs.impl.DataTransferJobImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class P2PDataSyncWorkflowIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScorpioAdapter scorpioAdapter;

    @Autowired
    private AuditRecordService auditRecordService;

    @Autowired
    private DataTransferJobImpl dataTransferJob;

    @LocalServerPort
    private int localServerPort;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    private MockWebServer mockWebServer;

    private List<MVEntity4DataNegotiation> initialMvEntity4DataNegotiationList;

    @DynamicPropertySource
    private static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @BeforeEach
    void setUp() throws IOException, JSONException {
        initialMvEntity4DataNegotiationList = createInitialEntitiesInScorpio();
        createInitialEntitiesInAuditRecord(auditRecordService, initialMvEntity4DataNegotiationList);
        startMockWebServer();
    }

    @AfterEach
    void setDown() throws IOException {
        removeInitialEntitiesInScorpio();
        removeInitialEntitiesInAuditRecord(auditRecordService, initialMvEntity4DataNegotiationList);
        stopMockWebServer();
    }

    @Test
    void itShouldReturnMissingExternalEntities() throws JsonProcessingException {
        DiscoverySyncRequest discoverySyncRequest = DiscoverySyncRequestMother.list1And2();
        Mono<DiscoverySyncRequest> discoverySyncRequestMono = Mono.just(discoverySyncRequest);

        String discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);
        System.out.println("Integration Test Request: " + discoverySyncRequestJson);

        DiscoverySyncResponse discoverySyncResponse = DiscoverySyncResponseMother.fromList(contextBrokerExternalDomain, initialMvEntity4DataNegotiationList);
        String expectedDiscoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        System.out.println("Integration Test Expected Response: " + expectedDiscoverySyncResponseJson);

        String response = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build()
                .post()
                .uri("/api/v1/sync/p2p/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .body(discoverySyncRequestMono, DiscoverySyncRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3).block();

        System.out.println("Integration Test Actual Response: " + response);

        assertEquals(expectedDiscoverySyncResponseJson, response);
    }

    @Test
    void itShouldUpsertExternalEntities() throws IOException, InterruptedException {
        var entitySyncResponse = EntitySyncResponseMother.sample2and4;
        mockWebServer.enqueue(new MockResponse()
                .setBody(entitySyncResponse));

        String issuer = "http://localhost:" + mockWebServer.getPort();

        DiscoverySyncRequest discoverySyncRequest = DiscoverySyncRequestMother.fullList(issuer);
        Mono<DiscoverySyncRequest> discoverySyncRequestMono = Mono.just(discoverySyncRequest);

        String discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);
        System.out.println("Integration Test Request: " + discoverySyncRequestJson);

        DiscoverySyncResponse discoverySyncResponse = DiscoverySyncResponseMother.fromList(contextBrokerExternalDomain, initialMvEntity4DataNegotiationList);
        String expectedDiscoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        System.out.println("Integration Test Expected Response: " + expectedDiscoverySyncResponseJson);

        Mono<String> responseMono = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build()
                .post()
                .uri("/api/v1/sync/p2p/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .body(discoverySyncRequestMono, DiscoverySyncRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3);

        StepVerifier
                .create(responseMono)
                .consumeNextWith(response -> {
                    assertEquals(expectedDiscoverySyncResponseJson, response);

                    try {
                        assertScorpioEntityIsExpected(EntitySyncResponseMother.id1, EntityMother.scorpioJson1());
                        assertScorpioEntityIsExpected(EntitySyncResponseMother.id2, EntityMother.scorpioJson2());
                        assertScorpioEntityIsExpected(EntitySyncResponseMother.id3, EntityMother.scorpioJson3());
                        assertScorpioEntityIsExpected(EntitySyncResponseMother.id4, EntityMother.scorpioJson4());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();


        RecordedRequest request = mockWebServer.takeRequest();
        System.out.println("Larequest: " + request);

        System.out.println("Integration Test Actual Response: " + responseMono);
    }

    @Test
    void itShouldCreateAuditRecord() throws IOException, InterruptedException {
        var entitySyncResponse = EntitySyncResponseMother.sample2and4;
        mockWebServer.enqueue(new MockResponse()
                .setBody(entitySyncResponse));

        String issuer = "http://localhost:" + mockWebServer.getPort();

        DiscoverySyncRequest discoverySyncRequest = DiscoverySyncRequestMother.fullList(issuer);
        Mono<DiscoverySyncRequest> discoverySyncRequestMono = Mono.just(discoverySyncRequest);

        String discoverySyncRequestJson = objectMapper.writeValueAsString(discoverySyncRequest);
        System.out.println("Integration Test Request: " + discoverySyncRequestJson);

        DiscoverySyncResponse discoverySyncResponse = DiscoverySyncResponseMother.fromList(contextBrokerExternalDomain, initialMvEntity4DataNegotiationList);
        String expectedDiscoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        System.out.println("Integration Test Expected Response: " + expectedDiscoverySyncResponseJson);

        Mono<String> responseMono = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build()
                .post()
                .uri("/api/v1/sync/p2p/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .body(discoverySyncRequestMono, DiscoverySyncRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3);

        StepVerifier
                .create(responseMono)
                .consumeNextWith(response -> {
                    assertEquals(expectedDiscoverySyncResponseJson, response);

                    assertAuditRecordEntityIsExpected(EntitySyncResponseMother.id1, MVEntity4DataNegotiationMother.sample1(), "http://example.org");
                    assertAuditRecordEntityIsExpected(EntitySyncResponseMother.id2, MVEntity4DataNegotiationMother.sample2(), issuer);
                    assertAuditRecordEntityIsExpected(EntitySyncResponseMother.id3, MVEntity4DataNegotiationMother.sample3(), "http://example.org");
                    assertAuditRecordEntityIsExpected(EntitySyncResponseMother.id4, MVEntity4DataNegotiationMother.sample4(), issuer);
                })
                .verifyComplete();


        RecordedRequest request = mockWebServer.takeRequest();
        System.out.println("Larequest: " + request);

        System.out.println("Integration Test Actual Response: " + responseMono);
    }

    private void assertScorpioEntityIsExpected(String entityId, String expectedEntityResponse) {
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

    private void assertAuditRecordEntityIsExpected(String entityId, MVEntity4DataNegotiation expectedMVEntity4DataNegotiation, String baseUri){
        await().atMost(10, TimeUnit.SECONDS).ignoreExceptions().until(() -> {
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

    private @NotNull List<MVEntity4DataNegotiation> createInitialEntitiesInScorpio() throws JSONException, JsonProcessingException {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        String responseEntities = EntityMother.list1And2OldAnd3();
        ScorpioInflator.addInitialEntitiesToContextBroker(brokerUrl, responseEntities);

        return MVEntity4DataNegotiationMother.list1And2OldAnd3();
    }

    private void removeInitialEntitiesInScorpio() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        List<String> ids = MVEntity4DataNegotiationMother.fullList().stream().map(MVEntity4DataNegotiation::id).toList();
        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);
    }

    private void createInitialEntitiesInAuditRecord(AuditRecordService auditRecordService, List<MVEntity4DataNegotiation> entities) {
        auditRecordService.fetchMostRecentAuditRecord().block();

        String processId = "0";
        String issuer = "http://example.org";
        for (var entity : entities) {
            auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, entity, AuditRecordStatus.PUBLISHED).block();
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

    private void startMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    private void stopMockWebServer() throws IOException {
        mockWebServer.shutdown();
    }
}
