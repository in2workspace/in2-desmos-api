package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.MVAuditServiceEntity4DataNegotiation;
import es.in2.desmos.domain.utils.ApplicationUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public final class MVAuditServiceEntity4DataNegotiationMother {
    private MVAuditServiceEntity4DataNegotiationMother() {
    }

    public static @NotNull MVAuditServiceEntity4DataNegotiation sample1() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_1);
    }

    public static @NotNull MVAuditServiceEntity4DataNegotiation sample1NullLifecyclestatus() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_1_NULL_LIFECYCLESTATUS);
    }

    public static @NotNull MVAuditServiceEntity4DataNegotiation sample4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_4);
    }

    private static MVAuditServiceEntity4DataNegotiation createSampleWithSameHashAndHashlink(String entityMotherJson) throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        JSONObject jsonObject = new JSONObject(entityMotherJson);
        String id = jsonObject.getString("id");
        String type = jsonObject.getString("type");
        String hash = ApplicationUtils.calculateSHA256(entityMotherJson);
        return new MVAuditServiceEntity4DataNegotiation(id, type, hash, hash);
    }
}
