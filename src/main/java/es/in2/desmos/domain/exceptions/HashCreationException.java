package es.in2.desmos.domain.exceptions;

public class HashCreationException extends RuntimeException {

    public HashCreationException(String message) {
        super(message);
    }

    public HashCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
