package es.in2.desmos.api.exception;

public class HashCreationException extends RuntimeException {

    public HashCreationException(String message) {
        super(message);
    }

    public HashCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
