package es.in2.desmos.objectmothers;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class UrlMother {
    private UrlMother() {
    }

    public static @NotNull List<String> example1And2urlsList() {
        List<String> urlExternalAccessNodesList = new ArrayList<>();
        urlExternalAccessNodesList.add("https://example1.org");
        urlExternalAccessNodesList.add("https://example2.org");
        return urlExternalAccessNodesList;
    }

    public static @NotNull String commaSeparatedExample1And2Urls() {
        return ("https://example1.org, https://example2.org");
    }
}
