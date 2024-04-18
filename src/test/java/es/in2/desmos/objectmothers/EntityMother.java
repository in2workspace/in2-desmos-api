package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.domain.models.Id;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class EntityMother {
    private EntityMother() {
    }

    public static @NotNull Map<@NotNull Id, @NotNull Entity> fullList(){
        Map<Id, Entity> fullList = new HashMap<>();
        fullList.put(new Id("urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb"),
                new Entity(unpretty(json1())));
        fullList.put(new Id("urn:productOfferingPrice:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87"),
                new Entity(unpretty(json2())));
        fullList.put(new Id("urn:productOfferingPrice:537e1ee3-0556-4fff-875f-e55bb97e7ab0"),
                new Entity(unpretty(json3())));
        fullList.put(new Id("urn:productOfferingPrice:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c"),
                new Entity(unpretty(json4())));
        return fullList;
    }

    public static String json1(){
        return """
                    {
                    "id": "urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb",
                    "type": "productOffering",
                    "version": "1.2",
                    "lastUpdate": "2024-09-05T12:00:00Z",
                    "productSpecification": {
                      "id": "spec-broadband-001",
                      "name": "1Gbps Broadband Spec"
                    },
                    "productOfferingPrice": {
                      "type": "Relationship",
                      "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                    }
                  }""";
    }

    public static String json2(){
        return """
                    {
                    "id": "urn:productOfferingPrice:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                    "type": "productOffering",
                    "version": "2.5",
                    "lastUpdate": "2024-07-09T12:00:00Z",
                    "productSpecification": {
                      "id": "spec-broadband-001",
                      "name": "1Gbps Broadband Spec"
                    },
                    "productOfferingPrice": {
                      "type": "Relationship",
                      "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                    }
                  }""";
    }

    public static String json3(){
        return """
                    {
                    "id": "urn:productOfferingPrice:537e1ee3-0556-4fff-875f-e55bb97e7ab0",
                    "type": "productOffering",
                    "version": "4.3",
                    "lastUpdate": "2024-04-03T12:00:00Z",
                    "productSpecification": {
                      "id": "spec-broadband-001",
                      "name": "1Gbps Broadband Spec"
                    },
                    "productOfferingPrice": {
                      "type": "Relationship",
                      "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                    }
                  }""";
    }

    public static String json4(){
        return """
                    {
                    "id": "urn:productOfferingPrice:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                    "type": "productOffering",
                    "version": "1.9",
                    "lastUpdate": "2024-06-02T12:00:00Z",
                    "productSpecification": {
                      "id": "spec-broadband-001",
                      "name": "1Gbps Broadband Spec"
                    },
                    "productOfferingPrice": {
                      "type": "Relationship",
                      "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                    }
                  }""";
    }

    public static String unpretty(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
