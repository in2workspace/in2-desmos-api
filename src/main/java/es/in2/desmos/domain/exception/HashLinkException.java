package es.in2.desmos.domain.exception;

public class HashLinkException extends RuntimeException {

    public HashLinkException(String message) {
        super(message);
    }

    public HashLinkException(String message, Throwable cause) {
        super(message, cause);
    }

}
