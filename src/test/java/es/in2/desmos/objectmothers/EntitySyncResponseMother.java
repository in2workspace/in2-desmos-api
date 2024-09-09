package es.in2.desmos.objectmothers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class EntitySyncResponseMother {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private EntitySyncResponseMother() {
    }

    public static String getSampleBase64() throws IOException {
        ArrayNode jsonArray = objectMapper.createArrayNode();

        for (var item : EntityMother.scorpioFullJsonArray()) {
            String sampleBase64 = Base64.getEncoder().encodeToString(item.getBytes());

            jsonArray.add(sampleBase64);
        }

        return objectMapper.writeValueAsString(jsonArray);
    }

    public static String getSampleWithCategoryBase64() throws IOException {
        ArrayNode jsonArray = objectMapper.createArrayNode();

        for (var item : EntityMother.scorpioFullWithCategoryJsonArray()) {
            String sampleBase64 = Base64.getEncoder().encodeToString(item.getBytes());

            jsonArray.add(sampleBase64);
        }

        return objectMapper.writeValueAsString(jsonArray);
    }

    public static String getSample2Base64() throws IOException {
        String sampleString = objectMapper.readTree(EntityMother.PRODUCT_OFFERING_2).toString();
        String sampleBase64 = Base64.getEncoder().encodeToString(sampleString.getBytes());
        List<String> sampleList = new ArrayList<>();
        sampleList.add(sampleBase64);
        return objectMapper.writeValueAsString(sampleList);
    }

    public static String getSample4Base64() throws IOException {
        String sampleString = objectMapper.readTree(EntityMother.PRODUCT_OFFERING_4).toString();
        String sampleBase64 = Base64.getEncoder().encodeToString(sampleString.getBytes());
        List<String> sampleList = new ArrayList<>();
        sampleList.add(sampleBase64);
        return objectMapper.writeValueAsString(sampleList);
    }

    public static String getEmptySampleBase64() throws IOException {
        String sampleBase64 = Base64.getEncoder().encodeToString("{}".getBytes());
        List<String> sampleList = new ArrayList<>();
        sampleList.add(sampleBase64);
        return objectMapper.writeValueAsString(sampleList);
    }
}
