package es.in2.desmos.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimestampFormatValidator implements ConstraintValidator<ValidTimestamp, long> {
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    public boolean isValid(long value, ConstraintValidatorContext context) {
        try {
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}