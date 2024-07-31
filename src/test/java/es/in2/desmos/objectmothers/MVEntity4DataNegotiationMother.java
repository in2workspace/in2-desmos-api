package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class MVEntity4DataNegotiationMother {
    private MVEntity4DataNegotiationMother() {
    }

    public static @NotNull MVEntity4DataNegotiation sample1() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb", "ProductOffering", "1.2", "2024-09-05T12:00:00Z", "89e62d6be87fd39dc19dc69a35d58d1ac2351854bf48a8264bf075643c89eddf", "fa54230df0a3a28e89ea00cc647ad5dace35bb80e94207072cd9e9cc01df7912f652");
    }

    public static @NotNull MVEntity4DataNegotiation sample1BadHash() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb", "ProductOffering", "1.2", "2024-09-05T12:00:00Z", "8ce0461d10e02556d3f16e21c8ac662c037f8b39efd059186b070f9aad8c00f0", "fa548ce0461d10e02556d3f16e21c8ac662c037f8b39efd059186b070f9aad8c00f0");
    }

    public static @NotNull MVEntity4DataNegotiation sample2() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87", "ProductOffering", "2.5", "2024-07-09T12:00:00Z", "eb2a3f823a26b77562ed81e9a6d1e8f9cbd5c9dd5f89b39923dfae7fd47ac818", "fa54eb2a3f823a26b77562ed81e9a6d1e8f9cbd5c9dd5f89b39923dfae7fd47ac818");
    }

    public static @NotNull MVEntity4DataNegotiation sample2VersionOld() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87", "ProductOffering", "2.1", "2024-07-09T12:00:00Z", "8bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sample3() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0", "ProductOffering", "4.3", "2024-04-03T12:00:00Z", "41332f3abafca9295930699a271b4e63de1acc166efe032bf04a6038fb18e8a8", "fa5441332f3abafca9295930699a271b4e63de1acc166efe032bf04a6038fb18e8a8");
    }

    public static @NotNull MVEntity4DataNegotiation sample3TimestampOld() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0", "ProductOffering", "4.3", "2020-02-01T12:00:00Z", "8bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f", "fa548bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f");
    }

    public static @NotNull MVEntity4DataNegotiation sample4() {
        return new MVEntity4DataNegotiation("urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", "f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12", "fa54f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12y");
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio1() {
        return getSampleScorpio(sample1(), "b203eda84ced16f5f67c1aa6e2c3db071a5b919cd4f6dc18a3d7c2013fedcd28", "fa547074ad3789791877a4429d478a57a91c34ec709719fb61211540a5786fb65cf6");
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio2() {
        return getSampleScorpio(sample2(), "d9d910ba4ec60f7a3fcfd73c9696d45b02a884d71b5ffd0ea0fae7b01ab5e0b7", "ac7b02549fc43a2e18cfbb5cb1be55fcfceb3a313efa4653419877df837a8913");
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio3() {
        return getSampleScorpio(sample3(), "5e5f28cfb7b6dee30c683849259bf3a1823736b65d5f1eaae9fd77f4aed74cb2", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleScorpio4() {
        return getSampleScorpio(sample4(), "c91e4fb89b21afca059a879d5d936f61331ecdbb2f61546af1c4abbafe6f27c6", "fa548d49b6143bbd40735d85fac87ee482162919ee51a01d7ed89dbd10f950ac6b6a");
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService1() {
        return getSampleScorpio(sample1(), "b203eda84ced16f5f67c1aa6e2c3db071a5b919cd4f6dc18a3d7c2013fedcd28", "fa547074ad3789791877a4429d478a57a91c34ec709719fb61211540a5786fb65cf6");
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService2Old() {
        return getSampleScorpio(sample2VersionOld(), "86a86ae657608e86529dce432932710b65cf8f62cda4ade599b0904fa0ba5dd9", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleDataSyncService3() {
        return getSampleScorpio(sample3(), "5e5f28cfb7b6dee30c683849259bf3a1823736b65d5f1eaae9fd77f4aed74cb2", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase1() {
        return getSampleScorpio(sample1(), "b7de2d6f9017fa91534e4d2dd97744afa43719c5d16e3af21c3ecf7c7c59ad3f", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop1() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c51", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", "cc3ea885443502a323113c9fcd703cbcb4b5f3413b47271216dc742a40ea9d87", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase2() {
        return getSampleScorpio(sample2(), "60ed4dab7d1c30baf865a698443383e9cf96a5496f635a89ca58839e822c2240", "fa544b34af6221ea9fd2f306c8d90e7a04e5020fd9a137180702e23f694281b8bc4e60ed4dab7d1c30baf865a698443383e9cf96a5496f635a89ca58839e822c2240");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase2Old() {
        return getSampleScorpio(sample2VersionOld(), "cc3ea885443502a323113c9fcd703cbcb4b5f3413b47271216dc742a40ea9d87", "fa544b34af6221ea9fd2f306c8d90e7a04e5020fd9a137180702e23f694281b8bc4e");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop2() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", "9a69533ae44995f511c926b6f443e9e9738041421cf5ddf0c8f5b31ffde310cf", "fa549a69533ae44995f511c926b6f443e9e9738041421cf5ddf0c8f5b31ffde310cf");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop2HashlinkHash() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", "2eed84c7d628d0ba3fc2f1606199c33ba28f5b3a7c9e0274cb8ac248cf6aea3f", "2eed84c7d628d0ba3fc2f1606199c33ba28f5b3a7c9e0274cb8ac248cf6aea3f");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop2Old() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c52", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", "17957d0dda5fcb2302da0cd16679a0d8215605ef30173df99c869464db338f3f", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase3() {
        return getSampleScorpio(sample3(), "04cdea6c3b403dbc6f4f0ff9916b4a034f1d070fe05b9f0fc4983da73f710571", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop3() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c53", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", "5db30a1dbb3804eaecc333130d72d1f596603c219c4b346dd0af602aedfecc78", "fa54");
    }

    public static @NotNull MVEntity4DataNegotiation sampleBase4() {
        return getSampleScorpio(sample4(), "c91e4fb89b21afca059a879d5d936f61331ecdbb2f61546af1c4abbafe6f27c6", "fa54c91e4fb89b21afca059a879d5d936f61331ecdbb2f61546af1c4abbafe6f27c6");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop4() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", "7c0cd001f472cd991c12fdf82683efa727016bf49c0eee24feb96cc22a1ab6f8", "fa547c0cd001f472cd991c12fdf82683efa727016bf49c0eee24feb96cc22a1ab6f8");
    }

    public static @NotNull MVEntity4DataNegotiation samplePop4HashlinkHash() {
        return new MVEntity4DataNegotiation("urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a", "ProductOfferingPrice", "1.3", "2024-09-11T14:50:00Z", "d3a8e094379d5e40da60701f20404391b40858d0aa0b5b4a96caeba8ef1b2d83", "d3a8e094379d5e40da60701f20404391b40858d0aa0b5b4a96caeba8ef1b2d83");
    }

    public static @NotNull MVEntity4DataNegotiation samplePrice() {
        return new MVEntity4DataNegotiation("urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b", "Price", "1.3", "2024-09-11T14:50:00Z", "f2ca059930791fcddaa387480cd722c64ba31d816a0255c2f89bf4b28def7680", "fa54f2ca059930791fcddaa387480cd722c64ba31d816a0255c2f89bf4b28def7680");
    }

    public static @NotNull MVEntity4DataNegotiation samplePriceHashlinkHash() {
        return new MVEntity4DataNegotiation("urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b", "Price", "1.3", "2024-09-11T14:50:00Z", "5d1fa98b6be265d04323c837a221d21412a1ab6d662043532e1a5116b3d7c488", "5d1fa98b6be265d04323c837a221d21412a1ab6d662043532e1a5116b3d7c488");
    }

    public static @NotNull MVEntity4DataNegotiation randomIdAndVersion() {
        String id = "urn:ProductOffering:" + UUID.randomUUID();
        Random random = new Random();
        double randomVersion = random.nextDouble() + 1;
        randomVersion = Math.round(randomVersion * 10.0) / 10.0;

        return new MVEntity4DataNegotiation(id, "ProductOffering", String.valueOf(randomVersion), "2024-04-01T12:00:00Z", generateRandomSha256(), generateRandomSha256());
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
        return new MVEntity4DataNegotiation(sample.id(), sample.type(), sample.version(), sample.lastUpdate(), hash, hashLink);
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
