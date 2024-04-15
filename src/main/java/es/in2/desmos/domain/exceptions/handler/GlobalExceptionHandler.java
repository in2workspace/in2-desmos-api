package es.in2.desmos.domain.exceptions.handler;

import es.in2.desmos.domain.exceptions.*;
import es.in2.desmos.domain.models.GlobalErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubscriptionCreationException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleBlockchainNodeSubscriptionException(SubscriptionCreationException ex, ServerHttpRequest request) {
        log.error("SubscriptionCreationException: {}", ex.getMessage());
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder().title("SubscriptionCreationException").message(ex.getMessage()).path(path).build());
    }

    @ExceptionHandler(BrokerNotificationParserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleBrokerNotificationParserException(BrokerNotificationParserException ex, ServerHttpRequest request) {
        log.error("BrokerNotificationParserException: {}", ex.getMessage());
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder().title("BrokerNotificationParserException").message(ex.getMessage()).path(path).build());
    }

    @ExceptionHandler(HashCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleHashCreationException(HashCreationException ex, ServerHttpRequest request) {
        log.error("HashCreationException: {}", ex.getMessage());
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder().title("HashCreationException").message(ex.getMessage()).path(path).build());
    }

    @ExceptionHandler(HashLinkException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleHashLinkException(HashLinkException ex, ServerHttpRequest request) {
        log.error("HashLinkException: {}", ex.getMessage());
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder().title("HashLinkException").message(ex.getMessage()).path(path).build());
    }

    @ExceptionHandler(JsonReadingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleJsonReadingException(JsonReadingException ex, ServerHttpRequest request) {
        log.error("JsonReadingException: {}", ex.getMessage());
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder().title("JsonReadingException").message(ex.getMessage()).path(path).build());
    }

    @ExceptionHandler(AuditRecordCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleAuditRecordCreationException(AuditRecordCreationException ex, ServerHttpRequest request) {
        log.error("AuditRecordCreationException: {}", ex.getMessage());
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder().title("AuditRecordCreationException").message(ex.getMessage()).path(path).build());
    }

    @ExceptionHandler(RequestErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleRequestErrorException(RequestErrorException ex, ServerHttpRequest request) {
        log.error("RequestErrorException: {}", ex.getMessage());
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder().title("RequestErrorException").message(ex.getMessage()).path(path).build());
    }

    @ExceptionHandler(BrokerEntityRetrievalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleBrokerEntityRetrievalException(BrokerEntityRetrievalException ex, ServerHttpRequest request) {
        log.error("BrokerEntityRetrievalException: {}", ex.getMessage());
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder().title("BrokerEntityRetrievalException").message(ex.getMessage()).path(path).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex, BindingResult bindingResult, ServerHttpRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}