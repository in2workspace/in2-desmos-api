package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalErrorMessageTests {

    private final String title = "title";
    private final String message = "message";
    private final String path = "path";

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        // Act
        GlobalErrorMessage globalErrorMessage = GlobalErrorMessage.builder()
                .title(title)
                .message(message)
                .path(path)
                .build();
        // Assert
        assertEquals(title, globalErrorMessage.title());
        assertEquals(message, globalErrorMessage.message());
        assertEquals(path, globalErrorMessage.path());
    }

    @Test
    void testToString() {
        // Arrange
        GlobalErrorMessage globalErrorMessage = GlobalErrorMessage.builder()
                .title(title)
                .message(message)
                .path(path)
                .build();
        // Act
        String result = globalErrorMessage.toString();
        // Assert
        assertTrue(result.contains(title));
        assertTrue(result.contains(message));
        assertTrue(result.contains(path));
    }

    @Test
    void testBlockchainTxPayloadBuilderToString() {
        // Arrange
        String expectedToString = "GlobalErrorMessage.GlobalErrorMessageBuilder(" +
                "title=title, message=message, path=path)";
        // Act
        GlobalErrorMessage.GlobalErrorMessageBuilder globalErrorMessageBuilder = GlobalErrorMessage.builder()
                .title(title)
                .message(message)
                .path(path);
        // Assert
        assertEquals(expectedToString, globalErrorMessageBuilder.toString());
    }


}