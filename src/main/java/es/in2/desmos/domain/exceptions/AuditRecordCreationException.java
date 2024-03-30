package es.in2.desmos.domain.exceptions;

public class AuditRecordCreationException extends RuntimeException {

    public AuditRecordCreationException(String message) {
        super(message);
    }

    public AuditRecordCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
