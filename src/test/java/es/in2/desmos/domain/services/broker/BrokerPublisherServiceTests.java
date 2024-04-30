package es.in2.desmos.domain.services.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import es.in2.desmos.domain.services.broker.impl.BrokerPublisherServiceImpl;
import es.in2.desmos.objectmothers.BrokerDataMother;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import es.in2.desmos.objectmothers.IdMother;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    void itShouldBatchUpsertEntitiesToContextBroker() {
        String processId = "0";

        String retrievedBrokerEntities = EntitySyncResponseMother.sample;

        when(brokerAdapterService.batchUpsertEntities(processId, retrievedBrokerEntities)).thenReturn(Mono.empty());

        Mono<Void> result = brokerPublisherService.batchUpsertEntitiesToContextBroker(processId, retrievedBrokerEntities);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(brokerAdapterService, times(1)).batchUpsertEntities(processId, retrievedBrokerEntities);
        verifyNoMoreInteractions(brokerAdapterService);
    }

    @Test
    void itShouldFindAllEntitiesFromListById() throws JSONException, JsonProcessingException {
        String processId = "0";
        Mono<List<Id>> idsMono = Mono.just(Arrays.stream(IdMother.entitiesRequest).toList());

        String entityRequestBrokerJson = BrokerDataMother.getEntityRequestBrokerJson;
        JSONArray expectedResponseJsonArray = new JSONArray(entityRequestBrokerJson);
        List<String> localEntities = new ArrayList<>();
        for (int i = 0; i < expectedResponseJsonArray.length(); i++) {
            String entity = expectedResponseJsonArray.getString(i);
            localEntities.add(entity);
        }
        JsonNode rootEntityJsonNode = objectMapper.readValue(BrokerDataMother.getEntityRequestBrokerJson, JsonNode.class);
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
    void itShouldNotReturnIdIfHasNotType() throws JSONException, JsonProcessingException {
        String processId = "0";
        List<Id> ids = new ArrayList<>();
        ids.add(new Id("urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0"));
        ids.add(new Id("urn:productOffering:06f56a54-9be9-4d45-bae7-2a036b721d27"));
        Mono<List<Id>> idsMono = Mono.just(ids);

        var brokerJson = BrokerDataMother.getEntityRequestBrokerNoTypeJson;
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
    void itShouldNotReturnIdIfHasNotRelationship() throws JSONException, JsonProcessingException {
        String processId = "0";
        List<Id> ids = new ArrayList<>();
        ids.add(new Id("urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0"));
        ids.add(new Id("urn:productOffering:06f56a54-9be9-4d45-bae7-2a036b721d27"));
        Mono<List<Id>> idsMono = Mono.just(ids);

        var brokerJson = BrokerDataMother.getEntityRequestBrokerNoRelationshipJson;
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
    void itShouldNotReturnIdIfHasNotObject() throws JSONException, JsonProcessingException {
        String processId = "0";
        List<Id> ids = new ArrayList<>();
        ids.add(new Id("urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0"));
        ids.add(new Id("urn:productOffering:06f56a54-9be9-4d45-bae7-2a036b721d27"));
        Mono<List<Id>> idsMono = Mono.just(ids);

        var brokerJson = BrokerDataMother.getEntityRequestBrokerNoObjectJson;

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