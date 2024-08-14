package es.in2.desmos.domain.exceptions;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException(String message) {
        super(message);
    }

}
