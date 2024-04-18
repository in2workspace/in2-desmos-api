//package es.in2.desmos.domain.validation;
//
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//@Target({ElementType.FIELD})
//@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = TimestampFormatValidator.class)
//public @interface ValidTimestamp {
//    String message() default "Invalid timestamp format";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//}