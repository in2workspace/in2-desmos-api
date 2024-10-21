package es.in2.desmos.domain.services.sync.services;

import es.in2.desmos.domain.utils.Base64Converter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base64ConverterServiceTests {

    @Test
    void itShouldConvertStringListToBase64List() {
        List<String> inputList = Arrays.asList("hello", "world");

        List<String> expected = Arrays.asList("aGVsbG8=", "d29ybGQ=");

        List<String> result = Base64Converter.convertStringListToBase64List(inputList);

        assertEquals(expected, result);
    }

    @Test
    void itShouldConvertBase64ToString() {
        String input = "aGVsbG8=";

        String result = Base64Converter.convertBase64ToString(input);

        assertEquals("hello", result);
    }
}