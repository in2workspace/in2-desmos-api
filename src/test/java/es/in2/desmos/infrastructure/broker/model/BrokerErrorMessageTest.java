package es.in2.desmos.infrastructure.broker.model;

import es.in2.desmos.infrastructure.broker.model.BrokerErrorMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BrokerErrorMessageTest {

    @Test
    void testBuilderAndAccessors() {
        // Arrange
        String type = "ErrorType";
        String title = "ErrorTitle";
        BrokerErrorMessage.Detail detail = new BrokerErrorMessage.Detail("ErrorDetailMessage");
        String errorCode = "ErrorCode123";
        // Act
        BrokerErrorMessage brokerErrorMessage = BrokerErrorMessage.builder()
                .type(type)
                .title(title)
                .detail(detail)
                .errorCode(errorCode)
                .build();
        // Assert
        assertEquals(type, brokerErrorMessage.type());
        assertEquals(title, brokerErrorMessage.title());
        assertEquals(detail, brokerErrorMessage.detail());
        assertEquals(errorCode, brokerErrorMessage.errorCode());
    }

    @Test
    void testDetailRecord() {
        // Arrange
        String detailMessage = "Detailed error message";
        // Act
        BrokerErrorMessage.Detail detail = new BrokerErrorMessage.Detail(detailMessage);
        // Assert
        assertEquals(detailMessage, detail.message());
    }

}

