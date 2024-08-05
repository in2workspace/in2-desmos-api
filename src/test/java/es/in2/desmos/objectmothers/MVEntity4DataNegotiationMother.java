package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import org.jetbrains.annotations.NotNull;

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

    public static @NotNull MVEntity4DataNegotiation sample1() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb", "ProductOffering", "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "89e62d6be87fd39dc19dc69a35d58d1ac2351854bf48a8264bf075643c89eddf", "fa54230df0a3a28e89ea00cc647ad5dace35bb80e94207072cd9e9cc01df7912f652");
    }

    public static @NotNull MVEntity4DataNegotiation sample1BadHash() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb", "ProductOffering", "1.2", "2024-09-05T12:00:00Z", LAUNCHED, VALID_FOR, "8ce0461d10e02556d3f16e21c8ac662c037f8b39efd059186b070f9aad8c00f0", "fa548ce0461d10e02556d3f16e21c8ac662c037f8b39efd059186b070f9aad8c00f0");
    }

    public static @NotNull MVEntity4DataNegotiation sample2() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87", "ProductOffering", "2.5", "2024-07-09T12:00:00Z", LAUNCHED, VALID_FOR, "eb2a3f823a26b77562ed81e9a6d1e8f9cbd5c9dd5f89b39923dfae7fd47ac818", "fa54eb2a3f823a26b77562ed81e9a6d1e8f9cbd5c9dd5f89b39923dfae7fd47ac818");
    }

    public static @NotNull MVEntity4DataNegotiation sample2VersionOld() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87", "ProductOffering", "2.1", "2024-07-09T12:00:00Z", LAUNCHED, VALID_FOR, "8bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sample3() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0", "ProductOffering", "4.3", "2024-04-03T12:00:00Z", LAUNCHED, VALID_FOR, "41332f3abafca9295930699a271b4e63de1acc166efe032bf04a6038fb18e8a8", "fa5441332f3abafca9295930699a271b4e63de1acc166efe032bf04a6038fb18e8a8");
    }

    public static @NotNull MVEntity4DataNegotiation sample3TimestampOld() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0", "ProductOffering", "4.3", "2020-02-01T12:00:00Z", LAUNCHED, VALID_FOR, "8bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f", "fa548bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f");
    }

    public static @NotNull MVEntity4DataNegotiation sample4() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", LAUNCHED, VALID_FOR, "f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12", "fa54f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12y");
    }

    public static @NotNull MVEntity4DataNegotiation sampleLaunched() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:1", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", LAUNCHED, VALID_FOR, "f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12", "fa54f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12y");
    }

    public static @NotNull MVEntity4DataNegotiation sampleRetired() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:2", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", "Retired", VALID_FOR, "f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12", "fa54f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12y");
    }

    public static @NotNull MVEntity4DataNegotiation sampleActive() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:2", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", "Active", VALID_FOR, "f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12", "fa54f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12y");
    }

    public static @NotNull MVEntity4DataNegotiation sampleCorrectValidFor() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:1", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", LAUNCHED, VALID_FOR, "f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12", "fa54f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12y");
    }

    public static @NotNull MVEntity4DataNegotiation sampleIncorrectValidFor() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:1", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", LAUNCHED, "2100-01-01T00:00:00.000Z", "f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12", "fa54f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12y");
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio1() {
        return getSampleScorpio(sample1(), "5ee5336326e2e49c09f08981891e3600d089c7210089efa0809493a01de15cba", "fa547074ad3789791877a4429d478a57a91c34ec709719fb61211540a5786fb65cf6");
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio2() {
        return getSampleScorpio(sample2(), "11e3066ebf4466ba44ec10c3140197265935f7cdb1cd0b468930017df99ba3e7", "7492b15a2f9bec24193bb193bb3718d25712c426fe21b5d49649d7c30631ff2e");
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio3() {
        return getSampleScorpio(sample3(), "8ef9cbb9bd057ab75dac62ee13aca54e1491e6b6c8f64d7ca15050ade0c2bcf5", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio4() {
        return getSampleScorpio(sample4(), "fbf101c522188d9663882bb0b143539463d2a034bf5622f3c85ffc35e2e15f03", "fa548d49b6143bbd40735d85fac87ee482162919ee51a01d7ed89dbd10f950ac6b6a");
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService1() {
        return getSampleScorpio(sample1(), "5ee5336326e2e49c09f08981891e3600d089c7210089efa0809493a01de15cba", "fa547074ad3789791877a4429d478a57a91c34ec709719fb61211540a5786fb65cf6");
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService2Old() {
        return getSampleScorpio(sample2VersionOld(), "86a86ae657608e86529dce432932710b65cf8f62cda4ade599b0904fa0ba5dd9", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService3() {
        return getSampleScorpio(sample3(), "8ef9cbb9bd057ab75dac62ee13aca54e1491e6b6c8f64d7ca15050ade0c2bcf5", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase1() {
        return getSampleScorpio(sample1(), "b7de2d6f9017fa91534e4d2dd97744afa43719c5d16e3af21c3ecf7c7c59ad3f", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop1() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c51", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "cc3ea885443502a323113c9fcd703cbcb4b5f3413b47271216dc742a40ea9d87", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase2() {
        return getSampleScorpio(sample2(), "13fe567ea61edaf196a86a3e6ec514ebaf09a21dfd35700c5b5fdbbb41a7bf0a", "d2f8cccf5d6be19125a8e8eb99a18512c102883d5e8ebe91a0059beef93a67a0");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase2Old() {
        return getSampleScorpio(sample2VersionOld(), "cc3ea885443502a323113c9fcd703cbcb4b5f3413b47271216dc742a40ea9d87", "fa544b34af6221ea9fd2f306c8d90e7a04e5020fd9a137180702e23f694281b8bc4e");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop2() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "9a69533ae44995f511c926b6f443e9e9738041421cf5ddf0c8f5b31ffde310cf", "fa549a69533ae44995f511c926b6f443e9e9738041421cf5ddf0c8f5b31ffde310cf");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop2HashlinkHash() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "63796f7b46d042a912cb778f5696491e45e6915af13b54985607686f21c38ec3", "63796f7b46d042a912cb778f5696491e45e6915af13b54985607686f21c38ec3");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop2Old() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c52", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "17957d0dda5fcb2302da0cd16679a0d8215605ef30173df99c869464db338f3f", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase3() {
        return getSampleScorpio(sample3(), "04cdea6c3b403dbc6f4f0ff9916b4a034f1d070fe05b9f0fc4983da73f710571", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop3() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c53", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "5db30a1dbb3804eaecc333130d72d1f596603c219c4b346dd0af602aedfecc78", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase4() {
        return getSampleScorpio(sample4(), "fbf101c522188d9663882bb0b143539463d2a034bf5622f3c85ffc35e2e15f03", "fa54c91e4fb89b21afca059a879d5d936f61331ecdbb2f61546af1c4abbafe6f27c6");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop4() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "7c0cd001f472cd991c12fdf82683efa727016bf49c0eee24feb96cc22a1ab6f8", "fa547c0cd001f472cd991c12fdf82683efa727016bf49c0eee24feb96cc22a1ab6f8");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop4HashlinkHash() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "e8541e4110e32394a1dee270d073cbdc34a54a96d23a6779cafd14a17ca4b0aa", "e8541e4110e32394a1dee270d073cbdc34a54a96d23a6779cafd14a17ca4b0aa");
    }

    public static @NotNull MVEntity4DataNegotiation samplePrice() {
        return new MVEntity4DataNegotiation("urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b", "Price", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "f2ca059930791fcddaa387480cd722c64ba31d816a0255c2f89bf4b28def7680", "fa54f2ca059930791fcddaa387480cd722c64ba31d816a0255c2f89bf4b28def7680");
    }

    public static @NotNull MVEntity4DataNegotiation samplePriceHashlinkHash() {
        return new MVEntity4DataNegotiation("urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b", "Price", "1.3", "2024-09-11T14:50:00Z", LAUNCHED, VALID_FOR, "16187e78cde007c8113ab2231c6aabe92d3e133ad634db4ef59d711533cc0fde", "16187e78cde007c8113ab2231c6aabe92d3e133ad634db4ef59d711533cc0fde");
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

    public static @NotNull List<MVEntity4DataNegotiation> list1And2() {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
        MVEntity4DataNegotiationList.add(sample1());
        MVEntity4DataNegotiationList.add(sample2());
        return MVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> list1And2OldAnd3() {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
        MVEntity4DataNegotiationList.add(sample1());
        MVEntity4DataNegotiationList.add(sample2VersionOld());
        MVEntity4DataNegotiationList.add(sample3());
        return MVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> listbadHash1And2() {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
        MVEntity4DataNegotiationList.add(sample1BadHash());
        MVEntity4DataNegotiationList.add(sample2());
        return MVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> list2And3() {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
        MVEntity4DataNegotiationList.add(sample2());
        MVEntity4DataNegotiationList.add(sample3());
        return MVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> list3And4() {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
        MVEntity4DataNegotiationList.add(sample3());
        MVEntity4DataNegotiationList.add(sample4());
        return MVEntity4DataNegotiationList;
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

    public static @NotNull List<MVEntity4DataNegotiation> fullList() {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
        MVEntity4DataNegotiationList.add(sample1());
        MVEntity4DataNegotiationList.add(sample2());
        MVEntity4DataNegotiationList.add(sample3());
        MVEntity4DataNegotiationList.add(sample4());
        return MVEntity4DataNegotiationList;
    }

    public static @NotNull List<MVEntity4DataNegotiation> randomList(int size) {
        List<MVEntity4DataNegotiation> initialEntities = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            initialEntities.add(MVEntity4DataNegotiationMother.randomIdAndVersion());
        }
        return initialEntities;
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
