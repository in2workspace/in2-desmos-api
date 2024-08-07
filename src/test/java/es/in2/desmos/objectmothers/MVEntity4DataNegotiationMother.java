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

    private MVEntity4DataNegotiationMother() {
    }

    public static @NotNull MVEntity4DataNegotiation sample1() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithScorpioHashlink(EntityMother.PRODUCT_OFFERING_1);
    }

    public static @NotNull MVEntity4DataNegotiation sample1BadHash() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        var sample = createSampleWithScorpioHashlink(EntityMother.PRODUCT_OFFERING_1);
        return new MVEntity4DataNegotiation(sample.id(), sample.type(), sample.version(), sample.lastUpdate(), sample.lifecycleStatus(), sample.validFor(), "8ce0461d10e02556d3f16e21c8ac662c037f8b39efd059186b070f9aad8c00f0", null);
    }

    public static @NotNull MVEntity4DataNegotiation sample2() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        var sample = createSampleWithScorpioHashlink(EntityMother.PRODUCT_OFFERING_2);
        var hashlink = ApplicationUtils.calculateHashLink(sample2VersionOld().hashlink(), sample.hash());
        return new MVEntity4DataNegotiation(sample.id(), sample.type(), sample.version(), sample.lastUpdate(), sample.lifecycleStatus(), sample.validFor(), sample.hash(), hashlink);
    }

    public static @NotNull MVEntity4DataNegotiation sample2VersionOld() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithScorpioHashlink(EntityMother.PRODUCT_OFFERING_2_OLD);
    }

    public static @NotNull MVEntity4DataNegotiation sample3() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        var sample = createSampleWithScorpioHashlink(EntityMother.PRODUCT_OFFERING_3);
        var hashlink = ApplicationUtils.calculateHashLink(sample3TimestampOld().hashlink(), sample.hash());
        return new MVEntity4DataNegotiation(sample.id(), sample.type(), sample.version(), sample.lastUpdate(), sample.lifecycleStatus(), sample.validFor(), sample.hash(), hashlink);
    }

    public static @NotNull MVEntity4DataNegotiation sample3TimestampOld() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithScorpioHashlink(EntityMother.PRODUCT_OFFERING_3_OLD);
    }

    public static @NotNull MVEntity4DataNegotiation sample4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        return createSampleWithScorpioHashlink(EntityMother.PRODUCT_OFFERING_4);
    }

    public static @NotNull MVEntity4DataNegotiation sampleLaunched() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:1", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", LAUNCHED, VALID_FOR, null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleRetired() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:2", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", "Retired", VALID_FOR, null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleActive() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:2", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", "Active", VALID_FOR, null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleCorrectValidFor() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:1", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", LAUNCHED, VALID_FOR, null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleIncorrectValidFor() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:1", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", LAUNCHED, "2100-01-01T00:00:00.000Z", null, null);
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio1() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
//        return getSampleScorpio(sample1(), "5ee5336326e2e49c09f08981891e3600d089c7210089efa0809493a01de15cba", "fa547074ad3789791877a4429d478a57a91c34ec709719fb61211540a5786fb65cf6");
        return sample1();
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio2() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
//        return getSamplScorpio(sample2(), "11e3066ebf4466ba44ec10c3140197265935f7cdb1cd0b468930017df99ba3e7", "7492b15a2f9bec24193bb193bb3718d25712c426fe21b5d49649d7c30631ff2e");
        return sample2();
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio3() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
//        return getSampleScorpio(sample3(), "8ef9cbb9bd057ab75dac62ee13aca54e1491e6b6c8f64d7ca15050ade0c2bcf5", "fa54");
        return sample3();
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
//        return getSampleScorpio(sample4(), "fbf101c522188d9663882bb0b143539463d2a034bf5622f3c85ffc35e2e15f03", "fa548d49b6143bbd40735d85fac87ee482162919ee51a01d7ed89dbd10f950ac6b6a");
        return sample4();
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService1() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
//        return getSampleScorpio(sample1(), "5ee5336326e2e49c09f08981891e3600d089c7210089efa0809493a01de15cba", "fa547074ad3789791877a4429d478a57a91c34ec709719fb61211540a5786fb65cf6");
        return sample1();
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService2Old() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
//        return getSampleScorpio(sample2VersionOld(), "86a86ae657608e86529dce432932710b65cf8f62cda4ade599b0904fa0ba5dd9", "fa54");
        return sample2VersionOld();
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService3() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
//        return getSampleScorpio(sample3(), "8ef9cbb9bd057ab75dac62ee13aca54e1491e6b6c8f64d7ca15050ade0c2bcf5", "fa54");
        return sample3();
    }

    public static @NotNull MVEntity4DataNegotiation category1() {
        return new MVEntity4DataNegotiation("urn:category:1", "category", "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "prova", "prova");
    }

    public static @NotNull MVEntity4DataNegotiation category2() {
        return new MVEntity4DataNegotiation("urn:category:2", "category", "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "prova", "prova");
    }

    public static @NotNull MVEntity4DataNegotiation catalog1() {
        return new MVEntity4DataNegotiation("urn:catalog:1", "catalog", "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "prova", "prova");
    }

    public static @NotNull MVEntity4DataNegotiation catalog2() {
        return new MVEntity4DataNegotiation("urn:catalog:2", "catalog", "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "prova", "prova");
    }

    public static @NotNull MVEntity4DataNegotiation randomIdAndVersion() {
        String id = "urn:ProductOffering:" + UUID.randomUUID();
        Random random = new Random();
        double randomVersion = random.nextDouble() + 1;
        randomVersion = Math.round(randomVersion * 10.0) / 10.0;

        return new MVEntity4DataNegotiation(id, "ProductOffering", String.valueOf(randomVersion), "2024-04-01T12:00:00Z", LAUNCHED, VALID_FOR, generateRandomSha256(), generateRandomSha256());
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

    private static MVEntity4DataNegotiation createSampleWithSameHashlink(String entityMotherJson) throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        JSONObject jsonObject = new JSONObject(entityMotherJson);
        String id = jsonObject.getString("id");
        String type = jsonObject.getString("type");
        String version = jsonObject.getString("version");
        String lastUpdate = jsonObject.getString("lastUpdate");
        String lifecycleStatus = jsonObject.getString("lifecycleStatus");
        String validFor = jsonObject.getJSONObject("validFor").getString("startDateTime");
        String hash = ApplicationUtils.calculateSHA256(entityMotherJson);
        return new MVEntity4DataNegotiation(id, type, version, lastUpdate, lifecycleStatus, validFor, hash, hash);
    }

    private static MVEntity4DataNegotiation createSampleWithScorpioHashlink(String entityMotherJson) throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
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

    private static MVEntity4DataNegotiation getSampleScorpio(MVEntity4DataNegotiation sample, String hash, String hashLink){
        return new MVEntity4DataNegotiation(sample.id(), sample.type(), sample.version(), sample.lastUpdate(), sample.lifecycleStatus(), sample.validFor(), hash, hashLink);
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
