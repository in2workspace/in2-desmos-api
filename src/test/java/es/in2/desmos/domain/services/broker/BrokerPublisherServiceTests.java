package es.in2.desmos.domain.services.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import es.in2.desmos.domain.services.broker.impl.BrokerPublisherServiceImpl;
import es.in2.desmos.objectmothers.BrokerDataMother;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.objectmothers.IdMother;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerPublisherServiceTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BrokerAdapterFactory brokerAdapterFactory;

    @Mock
    private BrokerAdapterService brokerAdapterService;

    private BrokerPublisherServiceImpl brokerPublisherService;

    @BeforeEach
    void init() {
        when(brokerAdapterFactory.getBrokerAdapter()).thenReturn(brokerAdapterService);
        brokerPublisherService = new BrokerPublisherServiceImpl(brokerAdapterFactory, objectMapper);
    }

    private final long id = 1234;
    private final String publisherAddress = "http://blockchain-testnode.infra.svc.cluster.local:8545/";
    private final String eventType = "ProductOffering";
    private final long timestamp = 1711801566;
    private final String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
            "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
            "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
    private final List<String> relevantMetadata = List.of("metadata1", "metadata2");
    private final String entityIdHash = "6f6468ded8276d009ab1b6c578c2b922053acd6b5a507f36d408d3f7c9ae91d0";
    private final String previousEntityHash = "98d9658d98764dbe135b316f52a98116b4b02f9d7e57212aa86335c42a58539a";

    BlockchainNotification blockchainNotification = BlockchainNotification.builder()
            .id(id)
            .publisherAddress(publisherAddress)
            .eventType(eventType)
            .timestamp(timestamp)
            .dataLocation(dataLocation)
            .relevantMetadata(relevantMetadata)
            .entityId(entityIdHash)
            .previousEntityHash(previousEntityHash)
            .build();

    @Test
    void testPublishDataToBroker() {
        //Arrange
        String processId = "processId";
        String retrievedBrokerEntity = "retrievedBrokerEntity";
        //Act
        when(brokerAdapterService.getEntityById(eq(processId), anyString())).thenReturn(Mono.just(""));
        when(brokerAdapterService.postEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.empty());
        //Assert
        StepVerifier.create(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                .verifyComplete();
    }

    @Test
    void testPublishDataToBrokerWithUpdate() {
        //Arrange
        String processId = "processId";
        String retrievedBrokerEntity = "retrievedBrokerEntity";
        //Act
        when(brokerAdapterService.getEntityById(eq(processId), anyString())).thenReturn(Mono.just("entityId"));
        when(brokerAdapterService.updateEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.empty());
        //Assert
        StepVerifier.create(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                .verifyComplete();
    }

    @Test
    void itShouldBatchUpsertEntitiesToContextBroker() throws JsonProcessingException, JSONException {
        String processId = "0";

        String retrievedBrokerEntities = EntityMother.getFullJsonList();

        when(brokerAdapterService.batchUpsertEntities(processId, retrievedBrokerEntities)).thenReturn(Mono.empty());

        Mono<Void> result = brokerPublisherService.batchUpsertEntitiesToContextBroker(processId, retrievedBrokerEntities);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(brokerAdapterService, times(1)).batchUpsertEntities(processId, retrievedBrokerEntities);
        verifyNoMoreInteractions(brokerAdapterService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            BrokerDataMother.getEntityRequestBrokerJson,
            BrokerDataMother.getEntityRequestWithSubentitiesArrayJson,
            BrokerDataMother.getEntityRequestWithSubentitiesArrayWithPropertyJson
    })
    void itShouldFindAllEntitiesFromListWithArrayOfSubentitiesById(String brokerJson) throws JSONException, JsonProcessingException {
        String processId = "0";
        Mono<List<Id>> idsMono = Mono.just(Arrays.stream(IdMother.entitiesRequest).toList());

        JSONArray expectedResponseJsonArray = new JSONArray(brokerJson);
        List<String> localEntities = new ArrayList<>();
        for (int i = 0; i < expectedResponseJsonArray.length(); i++) {
            String entity = expectedResponseJsonArray.getString(i);
            localEntities.add(entity);
        }
        JsonNode rootEntityJsonNode = objectMapper.readValue(brokerJson, JsonNode.class);
        when(brokerAdapterService.getEntityById(eq(processId), any())).thenAnswer(invocation -> {
            String entityId = invocation.getArgument(1);
            for (JsonNode rootEntityNodeChildren : rootEntityJsonNode) {
                if (rootEntityNodeChildren.has("id") && rootEntityNodeChildren.get("id").asText().equals(entityId)) {
                    return Mono.just(rootEntityNodeChildren.toString());
                }
            }
            return Mono.empty();
        });


        var resultMono = brokerPublisherService.findAllById(processId, idsMono);

        StepVerifier
                .create(resultMono)
                .consumeNextWith(result -> {
                    try {
                        String localEntitiesJson = getJsonNodeFromStringsList(localEntities).toString();
                        String resultJson = getJsonNodeFromStringsList(result).toString();

                        JSONAssert.assertEquals(localEntitiesJson, resultJson, false);
                    } catch (JsonProcessingException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            BrokerDataMother.getEntityRequestBrokerNoTypeJson,
            BrokerDataMother.getEntityRequestBrokerNoRelationshipJson,
            BrokerDataMother.getEntityRequestBrokerNoObjectJson
    })
    void itShouldNotReturnIdIfIncorrectEntity(String brokerJson) throws JSONException, JsonProcessingException {
        String processId = "0";
        List<Id> ids = new ArrayList<>();
        ids.add(new Id("urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0"));
        ids.add(new Id("urn:productOffering:06f56a54-9be9-4d45-bae7-2a036b721d27"));
        Mono<List<Id>> idsMono = Mono.just(ids);

        JSONArray expectedResponseJsonArray = new JSONArray(brokerJson);
        List<String> localEntities = new ArrayList<>();
        for (int i = 0; i < expectedResponseJsonArray.length(); i++) {
            String entity = expectedResponseJsonArray.getString(i);

            if (i != 2 && i != 4) {
                localEntities.add(entity);
            }
        }

        JsonNode rootEntityJsonNode = objectMapper.readValue(brokerJson, JsonNode.class);
        when(brokerAdapterService.getEntityById(eq(processId), any())).thenAnswer(invocation -> {
            String entityId = invocation.getArgument(1);
            for (JsonNode rootEntityNodeChildren : rootEntityJsonNode) {
                if (rootEntityNodeChildren.has("id") && rootEntityNodeChildren.get("id").asText().equals(entityId)) {
                    return Mono.just(rootEntityNodeChildren.toString());
                }
            }
            return Mono.empty();
        });


        var resultMono = brokerPublisherService.findAllById(processId, idsMono);

        StepVerifier
                .create(resultMono)
                .consumeNextWith(result -> {
                    try {
                        String localEntitiesJson = getJsonNodeFromStringsList(localEntities).toString();
                        String resultJson = getJsonNodeFromStringsList(result).toString();

                        JSONAssert.assertEquals(localEntitiesJson, resultJson, false);
                    } catch (JsonProcessingException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }

    @Test
    void itShouldThrowJsonProcessingExceptionWhenJsonIsIncorrect() {
        String processId = "0";
        List<Id> ids = new ArrayList<>();
        ids.add(new Id("urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0"));
        Mono<List<Id>> idsMono = Mono.just(ids);

        var brokerJson = """
                [""";

        when(brokerAdapterService.getEntityById(eq(processId), any())).thenReturn(Mono.just(brokerJson));


        var resultMono = brokerPublisherService.findAllById(processId, idsMono);

        StepVerifier.
                create(resultMono)
                .expectErrorMatches(throwable -> throwable instanceof JsonProcessingException)
                .verify();
    }

    private JsonNode getJsonNodeFromStringsList(List<String> localEntities) throws JsonProcessingException {
        List<JsonObject> localEntitiesObjects = new ArrayList<>();
        for (var entity : localEntities) {
            localEntitiesObjects.add(JsonParser.parseString(entity).getAsJsonObject());
        }

        return objectMapper.readTree(localEntitiesObjects.toString());
    }
}