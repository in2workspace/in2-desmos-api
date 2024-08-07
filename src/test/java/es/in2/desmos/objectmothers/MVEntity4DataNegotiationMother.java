package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.utils.ApplicationUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class MVEntity4DataNegotiationMother {

    public static final String LAUNCHED = "Launched";
    public static final String VALID_FOR = "2024-01-01T00:00:00.000Z";
    private static final String PRODUCT_OFFERING_TYPE_NAME = "ProductOffering";
    private static final String CATEGORY_TYPE_NAME = "category";
    private static final String CATALOG_TYPE_NAME = "catalog";

    private MVEntity4DataNegotiationMother() {
    }

    public static @NotNull MVEntity4DataNegotiation sample1() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_1);
    }

    public static @NotNull MVEntity4DataNegotiation sample1BadHash() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        var sample = createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_1);
        return new MVEntity4DataNegotiation(sample.id(), sample.type(), sample.version(), sample.lastUpdate(), sample.lifecycleStatus(), sample.validFor(), "8ce0461d10e02556d3f16e21c8ac662c037f8b39efd059186b070f9aad8c00f0", null);
    }

    public static @NotNull MVEntity4DataNegotiation sample2() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        var sample = createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_2);
        var hashlink = ApplicationUtils.calculateHashLink(sample2VersionOld().hashlink(), sample.hash());
        return new MVEntity4DataNegotiation(sample.id(), sample.type(), sample.version(), sample.lastUpdate(), sample.lifecycleStatus(), sample.validFor(), sample.hash(), hashlink);
    }

    public static @NotNull MVEntity4DataNegotiation sample2VersionOld() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_2_OLD);
    }

    public static @NotNull MVEntity4DataNegotiation sample3() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        var sample = createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_3);
        var hashlink = ApplicationUtils.calculateHashLink(sample3TimestampOld().hashlink(), sample.hash());
        return new MVEntity4DataNegotiation(sample.id(), sample.type(), sample.version(), sample.lastUpdate(), sample.lifecycleStatus(), sample.validFor(), sample.hash(), hashlink);
    }

    public static @NotNull MVEntity4DataNegotiation sample3TimestampOld() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_3_OLD);
    }

    public static @NotNull MVEntity4DataNegotiation sample4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithSameHashAndHashlink(EntityMother.PRODUCT_OFFERING_4);
    }

    public static @NotNull MVEntity4DataNegotiation sampleLaunched() {
        return new MVEntity4DataNegotiation("urn:" + PRODUCT_OFFERING_TYPE_NAME + ":1", PRODUCT_OFFERING_TYPE_NAME, "1.9", "2024-06-02T12:00:00Z", LAUNCHED, VALID_FOR, null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleRetired() {
        return new MVEntity4DataNegotiation("urn:" + PRODUCT_OFFERING_TYPE_NAME + ":2", PRODUCT_OFFERING_TYPE_NAME, "1.9", "2024-06-02T12:00:00Z", "Retired", VALID_FOR, null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleActive() {
        return new MVEntity4DataNegotiation("urn:" + PRODUCT_OFFERING_TYPE_NAME + ":2", PRODUCT_OFFERING_TYPE_NAME, "1.9", "2024-06-02T12:00:00Z", "Active", VALID_FOR, null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleCorrectValidFor() {
        return new MVEntity4DataNegotiation("urn:" + PRODUCT_OFFERING_TYPE_NAME + ":1", PRODUCT_OFFERING_TYPE_NAME, "1.9", "2024-06-02T12:00:00Z", LAUNCHED, VALID_FOR, null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleIncorrectValidFor() {
        return new MVEntity4DataNegotiation("urn:" + PRODUCT_OFFERING_TYPE_NAME + ":1", PRODUCT_OFFERING_TYPE_NAME, "1.9", "2024-06-02T12:00:00Z", LAUNCHED, "2100-01-01T00:00:00.000Z", null, null);
    }

    public static @NotNull MVEntity4DataNegotiation category1() {
        return new MVEntity4DataNegotiation("urn:" + CATEGORY_TYPE_NAME + ":1", CATEGORY_TYPE_NAME, "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "prova", "prova");
    }

    public static @NotNull MVEntity4DataNegotiation category2() {
        return new MVEntity4DataNegotiation("urn:" + CATEGORY_TYPE_NAME + ":2", CATEGORY_TYPE_NAME, "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "prova", "prova");
    }

    public static @NotNull MVEntity4DataNegotiation catalog1() {
        return new MVEntity4DataNegotiation("urn:" + CATALOG_TYPE_NAME + ":1", CATALOG_TYPE_NAME, "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "prova", "prova");
    }

    public static @NotNull MVEntity4DataNegotiation catalog2() {
        return new MVEntity4DataNegotiation("urn:" + CATALOG_TYPE_NAME + ":2", CATALOG_TYPE_NAME, "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "prova", "prova");
    }

    public static @NotNull MVEntity4DataNegotiation randomIdAndVersion() {
        String id = "urn:" + PRODUCT_OFFERING_TYPE_NAME + ":" + UUID.randomUUID();
        Random random = new Random();
        double randomVersion = random.nextDouble() + 1;
        randomVersion = Math.round(randomVersion * 10.0) / 10.0;

        return new MVEntity4DataNegotiation(id, PRODUCT_OFFERING_TYPE_NAME, String.valueOf(randomVersion), "2024-04-01T12:00:00Z", LAUNCHED, VALID_FOR, generateRandomSha256(), generateRandomSha256());
    }

    public static @NotNull List<MVEntity4DataNegotiation> list1And2() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<MVEntity4DataNegotiation> mVEntity4DataNegotiationList = new ArrayList<>();
        mVEntity4DataNegotiationList.add(sample1());
        mVEntity4DataNegotiationList.add(sample2());
        return mVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> list1And2OldAnd3() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<MVEntity4DataNegotiation> mVEntity4DataNegotiationList = new ArrayList<>();
        mVEntity4DataNegotiationList.add(sample1());
        mVEntity4DataNegotiationList.add(sample2VersionOld());
        mVEntity4DataNegotiationList.add(sample3());
        return mVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> listbadHash1And2() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<MVEntity4DataNegotiation> mVEntity4DataNegotiationList = new ArrayList<>();
        mVEntity4DataNegotiationList.add(sample1BadHash());
        mVEntity4DataNegotiationList.add(sample2());
        return mVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> list2And3() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<MVEntity4DataNegotiation> mVEntity4DataNegotiationList = new ArrayList<>();
        mVEntity4DataNegotiationList.add(sample2());
        mVEntity4DataNegotiationList.add(sample3());
        return mVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> list3And4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<MVEntity4DataNegotiation> mVEntity4DataNegotiationList = new ArrayList<>();
        mVEntity4DataNegotiationList.add(sample3());
        mVEntity4DataNegotiationList.add(sample4());
        return mVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> listCategories() {
        return List.of(category1(), category2());
    }

    public static @NotNull List<MVEntity4DataNegotiation> listCatalogs() {
        return List.of(catalog1(), catalog2());
    }

    public static @NotNull List<MVEntity4DataNegotiation> listLaunchedAndRetired() {
        return List.of(sampleLaunched(), sampleRetired());
    }

    public static @NotNull List<MVEntity4DataNegotiation> fullList() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<MVEntity4DataNegotiation> mVEntity4DataNegotiationList = new ArrayList<>();
        mVEntity4DataNegotiationList.add(sample1());
        mVEntity4DataNegotiationList.add(sample2());
        mVEntity4DataNegotiationList.add(sample3());
        mVEntity4DataNegotiationList.add(sample4());
        return mVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> randomList(int size) {
        List<MVEntity4DataNegotiation> initialEntities = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            initialEntities.add(MVEntity4DataNegotiationMother.randomIdAndVersion());
        }
        return initialEntities;
    }

    private static MVEntity4DataNegotiation createSampleWithSameHashAndHashlink(String entityMotherJson) throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        JSONObject jsonObject = new JSONObject(entityMotherJson);
        String id = jsonObject.getString("id");
        String type = jsonObject.getString("type");
        String version = jsonObject.getJSONObject("version").getString("value");
        String lastUpdate = jsonObject.getJSONObject("lastUpdate").getString("value");
        String lifecycleStatus = jsonObject.getJSONObject("lifecycleStatus").getString("value");
        String validFor = jsonObject.getJSONObject("validFor").getJSONObject("value").getString("startDateTime");
        String hash = ApplicationUtils.calculateSHA256(entityMotherJson);
        return new MVEntity4DataNegotiation(id, type, version, lastUpdate, lifecycleStatus, validFor, hash, hash);
    }

    private static String generateRandomSha256() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 64;
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
