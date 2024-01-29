package es.in2.desmos.broker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorMessageTest {

    @Test
    void testBuilderAndAccessors() {
        // Arrange
        String type = "ErrorType";
        String title = "ErrorTitle";
        ErrorMessage.Detail detail = new ErrorMessage.Detail("ErrorDetailMessage");
        String errorCode = "ErrorCode123";
        // Act
        ErrorMessage errorMessage = ErrorMessage.builder()
                .type(type)
                .title(title)
                .detail(detail)
                .errorCode(errorCode)
                .build();
        // Assert
        assertEquals(type, errorMessage.type());
        assertEquals(title, errorMessage.title());
        assertEquals(detail, errorMessage.detail());
        assertEquals(errorCode, errorMessage.errorCode());
    }

    @Test
    void testDetailRecord() {
        // Arrange
        String detailMessage = "Detailed error message";
        // Act
        ErrorMessage.Detail detail = new ErrorMessage.Detail(detailMessage);
        // Assert
        assertEquals(detailMessage, detail.message());
    }

}

