package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.ProductOffering;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ProductOfferingMother {
    private ProductOfferingMother() {
    }

    public static @NotNull ProductOffering sample1() {
        return new ProductOffering("urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb", "1.2", "2024-09-05T12:00:00Z");
    }

    public static @NotNull ProductOffering sample2() {
        return new ProductOffering("urn:productOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87", "2.5", "2024-07-09T12:00:00Z");
    }

    public static @NotNull ProductOffering sample3() {
        return new ProductOffering("urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0", "4.3", "2024-04-03T12:00:00Z");
    }

    public static @NotNull ProductOffering sample4() {
        return new ProductOffering("urn:productOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c", "1.9", "2024-06-02T12:00:00Z");
    }

    public static @NotNull List<ProductOffering> list3And4() {
        List<ProductOffering> productOfferingList = new ArrayList<>();
        productOfferingList.add(sample3());
        productOfferingList.add(sample4());
        return productOfferingList;
    }

    public static @NotNull List<ProductOffering> fullList() {
        List<ProductOffering> productOfferingList = new ArrayList<>();
        productOfferingList.add(sample1());
        productOfferingList.add(sample2());
        productOfferingList.add(sample3());
        productOfferingList.add(sample4());
        return productOfferingList;
    }
}
