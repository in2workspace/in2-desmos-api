package es.in2.desmos.it.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import es.in2.desmos.inflators.ScorpioInflator;
import es.in2.desmos.it.ContainerManager;
import es.in2.desmos.objectmothers.*;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"external-access-nodes.urls=http://localhost:49152 , http://localhost:49153"})
class DataSyncWorkflowIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScorpioAdapter scorpioAdapter;

    @Autowired
    private AuditRecordService auditRecordService;

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
    void itShouldUpsertEntitiesFromOtherAccessNodesWhenDiscoverySync() throws JSONException, IOException {
        try (MockWebServer mockWebServer2 = new MockWebServer();
             MockWebServer mockWebServer3 = new MockWebServer()) {

            mockWebServer2.start(49152);
            mockWebServer2.enqueue(new MockResponse()
                    .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .setBody(DiscoveryResponseMother.json2List()));

            mockWebServer3.start(49153);
            mockWebServer3.enqueue(new MockResponse()
                    .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .setBody(DiscoveryResponseMother.json4List()));

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

            assertScorpioEntityIsExpected(EntitySyncResponseMother.id1, EntityMother.scorpioJson1());
            assertScorpioEntityIsExpected(EntitySyncResponseMother.id2, EntityMother.scorpioJson2());
            assertScorpioEntityIsExpected(EntitySyncResponseMother.id3, EntityMother.scorpioJson3());
            assertScorpioEntityIsExpected(EntitySyncResponseMother.id4, EntityMother.scorpioJson4());
        }
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
                .expectNext(expectedDiscoverySyncResponseJson)
                .verifyComplete();

        try {
            assertScorpioEntityIsExpected(EntitySyncResponseMother.id1, EntityMother.scorpioJson1());
            assertScorpioEntityIsExpected(EntitySyncResponseMother.id2, EntityMother.scorpioJson2());
            assertScorpioEntityIsExpected(EntitySyncResponseMother.id3, EntityMother.scorpioJson3());
            assertScorpioEntityIsExpected(EntitySyncResponseMother.id4, EntityMother.scorpioJson4());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


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
                .expectNext(expectedDiscoverySyncResponseJson)
                .verifyComplete();

        assertAuditRecordEntityIsExpected(EntitySyncResponseMother.id1, MVEntity4DataNegotiationMother.sample1(), "http://example.org");
        assertAuditRecordEntityIsExpected(EntitySyncResponseMother.id2, MVEntity4DataNegotiationMother.sample2(), issuer);
        assertAuditRecordEntityIsExpected(EntitySyncResponseMother.id3, MVEntity4DataNegotiationMother.sample3(), "http://example.org");
        assertAuditRecordEntityIsExpected(EntitySyncResponseMother.id4, MVEntity4DataNegotiationMother.sample4(), issuer);


        RecordedRequest request = mockWebServer.takeRequest();
        System.out.println("Larequest: " + request);

        System.out.println("Integration Test Actual Response: " + responseMono);
    }

    @Test
    void itShouldReturnAllRequestedEntities() {
        givenEntitiesToRequestInScorpio();
        Mono<String> resultMono = whenUserRequestEntities();
        thenApplicationReturnRequestedEntities(resultMono);

        removeEntitiesToRequest();
    }

    private void givenEntitiesToRequestInScorpio() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        String entities = BrokerDataMother.getEntityRequestBrokerJson;
        ScorpioInflator.addInitialJsonEntitiesToContextBroker(brokerUrl, entities);
    }

    private Mono<String> whenUserRequestEntities() {
        Mono<Id[]> entitySyncRequest = Mono.just(IdMother.entitiesRequest);
        return WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build()
                .post()
                .uri("/api/v1/sync/p2p/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .body(entitySyncRequest, Id[].class)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3);
    }

    private void thenApplicationReturnRequestedEntities(Mono<String> resultMono) {
        StepVerifier
                .create(resultMono)
                .consumeNextWith(result -> {
                    try {
                        System.out.println("Result: " + result);
                        JSONAssert.assertEquals(BrokerDataMother.getEntityRequestBrokerJson, result, false);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }

    private void removeEntitiesToRequest() {
        String brokerUrl = ContainerManager.getBaseUriForScorpioA();
        List<String> ids = new ArrayList<>();
        ids.add("urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0");
        ids.add("urn:productOffering:06f56a54-9be9-4d45-bae7-2a036b721d27");
        ids.add("urn:productOffering:e8b7e5a7-5d0f-4c9b-b1e5-9b1af474207f");
        ids.add("urn:productOffering:d1c34fc5-0c2b-4022-94ab-d7cb99d8edc2");
        ids.add("urn:productOffering:39e31a28-583b-4f0d-80c6-6d7600cc9e36");
        ids.add("urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a");
        ids.add("urn:productOfferingPrice:a395344e-2c29-4d36-8463-0c0412f024d7");
        ids.add("urn:productOfferingPrice:cf36a34a-4e43-453c-bf8b-4a926ed59a0c");
        ids.add("urn:productOfferingPrice:ca9b5de4-bf5f-45de-8b33-0f2518f40e69");
        ids.add("urn:productOfferingPrice:faa692c0-1662-4fe2-b4e3-2d5ad86b47a1");
        ids.add("urn:price:2d5f3c16-4e77-45b3-8915-3da36b714e7b");
        ids.add("urn:price:6380d7c9-d9ec-4d35-865b-76e72d081cbf");
        ids.add("urn:price:ab87d164-3a6c-4b61-9b40-6f615b96d35d");
        ids.add("urn:priceAlteration:1bcaf091-16e3-4bbc-9800-a4636596384e");
        ids.add("urn:price:21e7f562-f62d-41b7-8243-1241d0f871c2");
        ids.add("urn:price:5a1e08b4-eb32-4b68-af44-aa35e6a40fb9");

        ScorpioInflator.deleteInitialEntitiesFromContextBroker(brokerUrl, ids);
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

    private void assertAuditRecordEntityIsExpected(String entityId, MVEntity4DataNegotiation expectedMVEntity4DataNegotiation, String baseUri) {
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
