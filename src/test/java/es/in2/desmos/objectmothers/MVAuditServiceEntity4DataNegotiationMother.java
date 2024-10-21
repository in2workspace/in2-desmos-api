package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.MVAuditServiceEntity4DataNegotiation;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.utils.ApplicationUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.List;

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

    public static @NotNull List<MVAuditServiceEntity4DataNegotiation> sample3and4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return List.of(
                createSampleWithSameHashAndHashlink(MVEntity4DataNegotiationMother.sample3()),
                createSampleWithSameHashAndHashlink(MVEntity4DataNegotiationMother.sample4())
        );
    }

    public static @NotNull List<MVAuditServiceEntity4DataNegotiation> sample3and4NewHashlink() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        MVEntity4DataNegotiation sample3 = MVEntity4DataNegotiationMother.sample3();

        var newHashlink = ApplicationUtils.calculateHashLink(AuditRecordMother.list3OtherHashTraderProducerAnd4().get(0).getEntityHashLink(), sample3.hash());

        return List.of(
                new MVAuditServiceEntity4DataNegotiation(sample3.id(), sample3.type(), sample3.hash(), newHashlink),
                createSampleWithSameHashAndHashlink(MVEntity4DataNegotiationMother.sample4())
        );
    }

    public static @NotNull List<MVAuditServiceEntity4DataNegotiation> sample3EqualsHashAndHashlinkAnd4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        MVEntity4DataNegotiation sample3 = MVEntity4DataNegotiationMother.sample3();

        return List.of(
                new MVAuditServiceEntity4DataNegotiation(sample3.id(), sample3.type(), sample3.hash(), sample3.hash()),
                createSampleWithSameHashAndHashlink(MVEntity4DataNegotiationMother.sample4())
        );
    }

    public static @NotNull List<MVAuditServiceEntity4DataNegotiation> listCategories() {
        var category1 = MVEntity4DataNegotiationMother.category1();
        var category2 = MVEntity4DataNegotiationMother.category2();

        return List.of(
                createSampleWithSameHashAndHashlink(category1),
                createSampleWithSameHashAndHashlink(category2)
        );
    }

    public static @NotNull List<MVAuditServiceEntity4DataNegotiation> listCatalogs() {
        var catalog1 = MVEntity4DataNegotiationMother.catalog1();
        var catalog2 = MVEntity4DataNegotiationMother.catalog2();

        return List.of(
                createSampleWithSameHashAndHashlink(catalog1),
                createSampleWithSameHashAndHashlink(catalog2)
        );
    }

    private static MVAuditServiceEntity4DataNegotiation createSampleWithSameHashAndHashlink(String entityMotherJson) throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        JSONObject jsonObject = new JSONObject(entityMotherJson);
        String id = jsonObject.getString("id");
        String type = jsonObject.getString("type");
        String hash = ApplicationUtils.calculateSHA256(entityMotherJson);
        return new MVAuditServiceEntity4DataNegotiation(id, type, hash, hash);
    }

    private static MVAuditServiceEntity4DataNegotiation createSampleWithSameHashAndHashlink(MVEntity4DataNegotiation mvEntity4DataNegotiation) {
        String id = mvEntity4DataNegotiation.id();
        String type = mvEntity4DataNegotiation.type();
        String hash = mvEntity4DataNegotiation.hash();
        String hashlink = mvEntity4DataNegotiation.hashlink();
        return new MVAuditServiceEntity4DataNegotiation(id, type, hash, hashlink);
    }
}
