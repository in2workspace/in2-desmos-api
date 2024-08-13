package es.in2.desmos.domain.exceptions;

public class InvalidIntegrityException extends RuntimeException {
    public InvalidIntegrityException(String message) {
        super(message);
    }
}
