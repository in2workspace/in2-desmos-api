package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class EntityMother {
    private EntityMother() {
    }

    public static String json1() throws JSONException {

        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        return getEntityJsonString(mvEntity4DataNegotiation);
    }

    public static String json2Old() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample2VersionOld();
        return getEntityJsonString(mvEntity4DataNegotiation);
    }

    public static String json3() throws JSONException {
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample3();
        return getEntityJsonString(mvEntity4DataNegotiation);
    }

    private static String getEntityJsonString(MVEntity4DataNegotiation mvEntity4DataNegotiation) throws JSONException {
        JSONObject productOffering = new JSONObject();

        productOffering.put("id", mvEntity4DataNegotiation.id());
        productOffering.put("type", mvEntity4DataNegotiation.type());
        productOffering.put("version", mvEntity4DataNegotiation.version());
        productOffering.put("lastUpdate", mvEntity4DataNegotiation.lastUpdate());

        JSONObject productSpecification = new JSONObject();
        productSpecification.put("id", "spec-broadband-001");
        productSpecification.put("name", "1Gbps Broadband Spec");

        productOffering.put("productSpecification", productSpecification);

        JSONObject productOfferingPrice = new JSONObject();
        productOfferingPrice.put("type", "Relationship");
        productOfferingPrice.put("object", "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a");

        productOffering.put("productOfferingPrice", productOfferingPrice);

        return productOffering.toString();
    }

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
        productOfferingPrice.put("object", "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a");

        productOffering.put("productOfferingPrice", productOfferingPrice);

        return productOffering.toString();
    }

    private static JSONObject getScorpioProperty(String value) throws JSONException {
        JSONObject scorpioProperty = new JSONObject();
        scorpioProperty.put("type", "Property");
        scorpioProperty.put("value", value);
        return scorpioProperty;
    }

    public static String list1And2OldAnd3() throws JSONException, JsonProcessingException {
        List<String> jsonObjects = new ArrayList<>();
        jsonObjects.add(json1());
        jsonObjects.add(json2Old());
        jsonObjects.add(json3());

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(jsonObjects);
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
