package es.in2.desmos.domain.exception;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestErrorExceptionTest {

    @Test
    void testErrorMessageIsPropagated() {
        String errorMessage = "This is an error message";
        RequestErrorException exception = new RequestErrorException(errorMessage);

        assertEquals(errorMessage, exception.getMessage(), "The error message should match the one provided");
    }

    @Test
    void testErrorCauseIsPropagated() {
        String errorMessage = "This is an error message";
        Throwable cause = new RuntimeException("This is the cause");

        RequestErrorException exception = new RequestErrorException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage(), "The error message should match the one provided");
        assertNotNull(exception.getCause(), "The cause should not be null");
        assertEquals(cause, exception.getCause(), "The cause should match the one provided");
        assertTrue(exception.getCause() instanceof RuntimeException, "The cause should be an instance of RuntimeException");
    }
}

