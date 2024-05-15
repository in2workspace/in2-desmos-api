package es.in2.desmos.domain.exceptions;

public class InvalidSyncResponseException extends RuntimeException {
    public InvalidSyncResponseException(String message) {
        super(message);
    }
}
