package es.in2.desmos.api.exception;

public class BrokerNotificationParserException extends RuntimeException {

    public BrokerNotificationParserException(String message) {
        super(message);
    }

    public BrokerNotificationParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
