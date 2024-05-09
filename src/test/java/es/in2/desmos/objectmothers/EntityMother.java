package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public final class EntityMother {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private EntityMother() {
    }

    public static @NotNull String getFullJsonList() throws JsonProcessingException {
        return compressJson(fullJsonList);
    }

    public static String getJsonList1And2OldAnd3() throws JsonProcessingException {
        return compressJson(jsonList1And2OldAnd3);
    }

    public static String getListJson2And4() throws JsonProcessingException {
        return compressJson(listJson2And4);
    }

    public static String getJson2() throws JsonProcessingException {
        return compressJson(json2);
    }

    public static String getJson4() throws JsonProcessingException {
        return compressJson(json4);
    }

    private static final @NotNull String fullJsonList = """
            [
                 {
                     "id": "urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb",
                     "type": "ProductOffering",
                     "version": "1.2",
                     "lastUpdate": "2024-09-05T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 },
                 {
                     "id": "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                     "type": "ProductOffering",
                     "version": "2.5",
                     "lastUpdate": "2024-07-09T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 },
                 {
                     "id": "urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0",
                     "type": "ProductOffering",
                     "version": "4.3",
                     "lastUpdate": "2024-04-03T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 },
                 {
                     "id": "urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                     "type": "ProductOffering",
                     "version": "1.9",
                     "lastUpdate": "2024-06-02T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 }
             ]""";

    private static final String jsonList1And2OldAnd3 = """
            [
                {
                         "id": "urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb",
                         "type": "ProductOffering",
                         "version": "1.2",
                         "lastUpdate": "2024-09-05T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     },
                     {
                         "id": "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                         "type": "ProductOffering",
                         "version": "2.1",
                         "lastUpdate": "2024-07-09T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     },
                     {
                         "id": "urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0",
                         "type": "ProductOffering",
                         "version": "4.3",
                         "lastUpdate": "2024-04-03T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     }
            ]""";

    private static final String listJson2And4 = """
            [
                 {
                     "id": "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                     "type": "ProductOffering",
                     "version": "2.5",
                     "lastUpdate": "2024-07-09T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 },
                 {
                     "id": "urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                     "type": "ProductOffering",
                     "version": "1.9",
                     "lastUpdate": "2024-06-02T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 }
             ]""";

    private static final String json2 = """
            {
                     "id": "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                     "type": "ProductOffering",
                     "version": "2.5",
                     "lastUpdate": "2024-07-09T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 }""";

    private static final String json4 = """
            {
                     "id": "urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                     "type": "ProductOffering",
                     "version": "1.9",
                     "lastUpdate": "2024-06-02T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 }""";

    private static String getEntityJsonScorpioString(MVEntity4DataNegotiation mvEntity4DataNegotiation) throws JSONException {
        JSONObject productOffering = new JSONObject();

        productOffering.put("id", mvEntity4DataNegotiation.id());
        productOffering.put("type", mvEntity4DataNegotiation.type());

        var versionProperty = getScorpioProperty(mvEntity4DataNegotiation.version());
        productOffering.put("version", versionProperty);

        var lastUpdateProperty = getScorpioProperty(mvEntity4DataNegotiation.lastUpdate());
        productOffering.put("lastUpdate", lastUpdateProperty);

        JSONObject productSpecificationValue = new JSONObject();
        productSpecificationValue.put("id", "spec-broadband-001");
        productSpecificationValue.put("name", "1Gbps Broadband Spec");

        JSONObject productSpecification = new JSONObject();
        productSpecification.put("type", "Property");
        productSpecification.put("value", productSpecificationValue);

        productOffering.put("productSpecification", productSpecification);

        JSONObject productOfferingPrice = new JSONObject();
        productOfferingPrice.put("type", "Relationship");
        productOfferingPrice.put("object", "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a");

        productOffering.put("productOfferingPrice", productOfferingPrice);

        return productOffering.toString();
    }

    private static JSONObject getScorpioProperty(String value) throws JSONException {
        JSONObject scorpioProperty = new JSONObject();
        scorpioProperty.put("type", "Property");
        scorpioProperty.put("value", value);
        return scorpioProperty;
    }

    private static String compressJson(String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json);
        return objectMapper.writeValueAsString(jsonNode);
    }

    public static String scorpioJson1() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation);
    }

    public static String scorpioJson2() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample2();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation);
    }

    public static String scorpioJson3() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample3();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation);
    }

    public static String scorpioJson4() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample4();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation);
    }
}
