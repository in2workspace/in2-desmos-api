package es.in2.desmos.objectmothers;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class EntitySyncResponseMother {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private EntitySyncResponseMother() {
    }

    public static List<String> getSampleBase64() {
        List<String> sample = new ArrayList<>();

        for (var item : EntityMother.scorpioFullJsonArray()) {
            String sampleBase64 = Base64.getEncoder().encodeToString(item.getBytes());

            sample.add(sampleBase64);
        }

        return sample;
    }

    public static List<String> getSampleWithCategoryBase64() {
        List<String> sample = new ArrayList<>();

        for (var item : EntityMother.scorpioFullWithCategoryJsonArray()) {
            String sampleBase64 = Base64.getEncoder().encodeToString(item.getBytes());

            sample.add(sampleBase64);
        }

        return sample;
    }

    public static List<String> getSample2Base64() throws IOException {
        String sampleString = objectMapper.readTree(EntityMother.PRODUCT_OFFERING_2).toString();
        String sampleBase64 = Base64.getEncoder().encodeToString(sampleString.getBytes());
        return List.of(sampleBase64);
    }

    public static List<String> getSample4Base64() throws IOException {
        String sampleString = objectMapper.readTree(EntityMother.PRODUCT_OFFERING_4).toString();
        String sampleBase64 = Base64.getEncoder().encodeToString(sampleString.getBytes());
        return List.of(sampleBase64);
    }
}
