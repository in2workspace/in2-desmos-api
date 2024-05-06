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
        return new MVEntity4DataNegotiation("urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87", "ProductOffering", "2.5", "2024-07-09T12:00:00Z", "08b236983ba01bbcd268793b104917f89f0bba8160d2f693911087c72b9a8051", "fa5408b236983ba01bbcd268793b104917f89f0bba8160d2f693911087c72b9a8051");
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
        return new MVEntity4DataNegotiation("urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", "cbcd525a6f2b5b6cefd8b4d9e83ac1e32946f5388634f012736bd6d543f0d017", "fa54cbcd525a6f2b5b6cefd8b4d9e83ac1e32946f5388634f012736bd6d543f0d017");
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

    public static List<MVEntity4DataNegotiation> list2() {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
        MVEntity4DataNegotiationList.add(sample2());
        return MVEntity4DataNegotiationList;
    }

    public static List<MVEntity4DataNegotiation> list4() {
        List<MVEntity4DataNegotiation> MVEntity4DataNegotiationList = new ArrayList<>();
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
