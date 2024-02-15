package es.in2.desmos.api.exception;

public class RequestErrorException extends RuntimeException {

    public RequestErrorException(String message) {
        super(message);
    }

    public RequestErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
