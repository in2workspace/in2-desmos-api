package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    public static String getJsonList1And2OldAnd3AndSubOfferings() throws JsonProcessingException {
        return compressJson(jsonList1And2OldAnd3AndSubOfferings);
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
                     }
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
                     }
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
                     }
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
                     }
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
                         }
                     },
                     {
                         "id": "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                         "type": "ProductOffering",
                         "version": "2.1",
                         "lastUpdate": "2024-07-09T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         }
                     },
                     {
                         "id": "urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0",
                         "type": "ProductOffering",
                         "version": "4.3",
                         "lastUpdate": "2024-04-03T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         }
                     }
            ]""";

    private static final String jsonList1And2OldAnd3AndSubOfferings = """
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
                             "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c51"
                         }
                     },
                     {
                      "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c51",
                      "type": "ProductOfferingPrice",
                      "version": "1.3",
                      "lastUpdate": "2024-09-11T14:50:00Z",
                      "value": "5"
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
                             "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c52"
                         }
                     },
                     {
                      "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c52",
                      "type": "ProductOfferingPrice",
                      "version": "1.3",
                      "lastUpdate": "2024-09-11T14:50:00Z",
                      "value": "5"
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
                             "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c53"
                         }
                     },
                     {
                      "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c53",
                      "type": "ProductOfferingPrice",
                      "version": "1.3",
                      "lastUpdate": "2024-09-11T14:50:00Z",
                      "value": "5"
                 }
            ]""";

    private static final String listJson2And4AndSubOfferings = """
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
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b"
                     }
                 },
                 {
                      "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b",
                      "type": "ProductOfferingPrice",
                      "version": "1.3",
                      "lastUpdate": "2024-09-11T14:50:00Z",
                      "value": "5"
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
                     }
                 },
                 {
                      "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a",
                      "type": "ProductOfferingPrice",
                      "lastUpdate": {
                          "type": "Property",
                          "value": "2024-09-11T14:50:00Z"
                      },
                      "price": {
                          "type": "Relationship",
                          "object": "urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b"
                      },
                      "version": {
                          "type": "Property",
                          "value": "1.3"
                      }
                  },
                 {
                      "id": "urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b",
                      "type": "Price",
                      "version": "2.1",
                      "lastUpdate": "2024-06-03T12:00:00Z"
                 }
             ]""";

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
                     }
                 }""";

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
                     }
                 }""";

    public static String getListJson2And4AndSubOfferings() throws JsonProcessingException {
        return compressJson(listJson2And4AndSubOfferings);
    }

    private static String getEntityJsonScorpioString(MVEntity4DataNegotiation mvEntity4DataNegotiation, String productOfferingPriceValue) throws JSONException {
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
        productOfferingPrice.put("object", productOfferingPriceValue);

        productOffering.put("productOfferingPrice", productOfferingPrice);

        return productOffering.toString();
    }

    private static String getEntityJsonScorpioStringWithoutRelationship(MVEntity4DataNegotiation mvEntity4DataNegotiation) throws JSONException {
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

    public static String scorpioFullJsonList() throws JSONException, JsonProcessingException {
        var scorpioJsonNode1 = objectMapper.readTree(scorpioDefaultJson1());
        var scorpioJsonNode2 = objectMapper.readTree(scorpioDefaultJson2());
        var scorpioJsonNode3 = objectMapper.readTree(scorpioDefaultJson3());
        var scorpioJsonNode4 = objectMapper.readTree(scorpioDefaultJson4());

        ArrayNode jsonArray = objectMapper.createArrayNode();
        jsonArray.add(scorpioJsonNode1);
        jsonArray.add(scorpioJsonNode2);
        jsonArray.add(scorpioJsonNode3);
        jsonArray.add(scorpioJsonNode4);

        return objectMapper.writeValueAsString(jsonArray);
    }

    public static String scorpioJson2And4() throws JSONException, JsonProcessingException {
        var scorpioJsonNode2 = objectMapper.readTree(scorpioDefaultJson2());
        var scorpioJsonNode4 = objectMapper.readTree(scorpioDefaultJson4());

        ArrayNode jsonArray = objectMapper.createArrayNode();
        jsonArray.add(scorpioJsonNode2);
        jsonArray.add(scorpioJsonNode4);

        return objectMapper.writeValueAsString(jsonArray);
    }

    public static String scorpioDefaultJson1() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation, "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a");
    }

    public static String scorpioDefaultJson2() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample2();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation, "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a");
    }

    public static String scorpioDefaultJson3() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample3();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation, "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a");
    }

    public static String scorpioDefaultJson4() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample4();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation, "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a");
    }

    public static String scorpioJson1(String productOfferingPriceValue) throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation, productOfferingPriceValue);
    }

    public static String scorpioJson2(String productOfferingPriceValue) throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample2();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation, productOfferingPriceValue);
    }

    public static String scorpioJson3(String productOfferingPriceValue) throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample3();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation, productOfferingPriceValue);
    }

    public static String scorpioJson4(String productOfferingPriceValue) throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample4();
        return getEntityJsonScorpioString(mvEntity4DataNegotiation, productOfferingPriceValue);
    }

    public static String scorpioJson1WithoutRelationship() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        return getEntityJsonScorpioStringWithoutRelationship(mvEntity4DataNegotiation);
    }

    public static String scorpioJson2WithoutRelationship() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample2();
        return getEntityJsonScorpioStringWithoutRelationship(mvEntity4DataNegotiation);
    }

    public static String scorpioJson3WithoutRelationship() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample3();
        return getEntityJsonScorpioStringWithoutRelationship(mvEntity4DataNegotiation);
    }

    public static String scorpioJson4WithoutRelationship() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample4();
        return getEntityJsonScorpioStringWithoutRelationship(mvEntity4DataNegotiation);
    }

    public static final String scorpioJsonPop1 = """
            {
                "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c51",
                "type": "ProductOfferingPrice",
                "lastUpdate": {
                    "type": "Property",
                    "value": "2024-09-11T14:50:00Z"
                },
                "version": {
                    "type": "Property",
                    "value": "1.3"
                },
                "value": "5"
            }""";

    public static final String scorpioJsonPop2 = """
            {
                "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b",
                "type": "ProductOfferingPrice",
                "lastUpdate": {
                    "type": "Property",
                    "value": "2024-09-11T14:50:00Z"
                },
                "version": {
                    "type": "Property",
                    "value": "1.3"
                },
                "value": "5"
            }""";

    public static final String scorpioJsonPop3 = """
            {
                "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c53",
                "type": "ProductOfferingPrice",
                "lastUpdate": {
                    "type": "Property",
                    "value": "2024-09-11T14:50:00Z"
                },
                "version": {
                    "type": "Property",
                    "value": "1.3"
                },
                "value": "5"
            }""";

    public static final String scorpioJsonPop4 = """
            {
                 "id": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a",
                 "type": "ProductOfferingPrice",
                 "lastUpdate": {
                     "type": "Property",
                     "value": "2024-09-11T14:50:00Z"
                 },
                 "price": {
                     "type": "Relationship",
                     "object": "urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b"
                 },
                 "version": {
                     "type": "Property",
                     "value": "1.3"
                 }
             }""";

    public static final String scorpioJsonPrice = """
            {
                 "id": "urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b",
                 "type": "Price",
                 "lastUpdate": {
                     "type": "Property",
                     "value": "2024-06-03T12:00:00Z"
                 },
                 "version": {
                     "type": "Property",
                     "value": "2.1"
                 }
             }""";
}
