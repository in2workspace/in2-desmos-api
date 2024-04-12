package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.Entity;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class EntityMother {
    private EntityMother() {
    }

    public static @NotNull Entity sample1() {
        return new Entity("urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb", "ProductOffering", "1.2", "2024-09-05T12:00:00Z", "5a81a1e3cf6adcb722d5ae1b7b67c07240e72b095903ee2451dcdf0630f27c5e", "d4a84a8c86bf91e13e0892ddab8d4728bbd27bf6629a7adbc99b79a10e86f6f5");
    }

    public static @NotNull Entity sample2() {
        return new Entity("urn:productOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87", "ProductOffering", "2.5", "2024-07-09T12:00:00Z", "8bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f", "5911ae4cf4e8f76cb27605c5e4898a8fc66dc695a44f0dbca24c139e6c304d61");
    }

    public static @NotNull Entity sample3() {
        return new Entity("urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0", "ProductOffering", "4.3", "2024-04-03T12:00:00Z", "8bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f", "f9cf16f663aa8d06c1cb144ca92e55a1f971118f4663b2c6369c239d87c21c27");
    }

    public static @NotNull Entity sample4() {
        return new Entity("urn:productOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c", "ProductOffering", "1.9", "2024-06-02T12:00:00Z", "8bc17b3e9f6e3d54e3f5b63e4a8826a28bba7d03d0f46c7a79b1f4d13eb4ee2f", "7c5de38264ef68c13e1c9d8eab32fd4257ff6a5f447f28c74f5414e8a034b325");
    }

    public static @NotNull Entity randomIdAndVersion() {
        String id = "urn:productOffering:" + UUID.randomUUID();
        Random random = new Random();
        double randomVersion = random.nextDouble() + 1;
        randomVersion = Math.round(randomVersion * 10.0) / 10.0;

        return new Entity(id, "ProductOffering", String.valueOf(randomVersion), "2024-04-01T12:00:00Z", generateRandomSha256(), generateRandomSha256());
    }

    public static @NotNull List<Entity> list1And2() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(sample1());
        entityList.add(sample2());
        return entityList;
    }

    public static @NotNull List<Entity> list3And4() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(sample3());
        entityList.add(sample4());
        return entityList;
    }

    public static @NotNull List<Entity> randomList(int size) {
        List<Entity> initialEntities = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            initialEntities.add(EntityMother.randomIdAndVersion());
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
