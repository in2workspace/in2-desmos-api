package es.in2.desmos.domain.utils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class Base64Converter {

    private Base64Converter() {
    }

    public static List<String> convertStringListToBase64List(List<String> items) {
        List<String> encodedStrings = new ArrayList<>();

        for(var item: items){
            String encodedItem = Base64.getEncoder().encodeToString(item.getBytes());

            encodedStrings.add(encodedItem);
        }

        return encodedStrings;
    }

    public static String convertBase64ToString(String item) {
        var decodedBytes = Base64.getDecoder().decode(item.getBytes());
        return new String(decodedBytes);
    }
}
