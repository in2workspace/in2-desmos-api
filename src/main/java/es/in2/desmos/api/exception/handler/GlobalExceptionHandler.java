package es.in2.desmos.api.exception.handler;

import es.in2.desmos.api.exception.*;
import es.in2.desmos.api.model.GlobalErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubscriptionCreationException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleBlockchainNodeSubscriptionException(SubscriptionCreationException ex,
                                                                              ServerHttpRequest request) {
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder()
                .title("SubscriptionCreationException")
                .message(ex.getMessage())
                .path(path)
                .build());
    }

    @ExceptionHandler(BrokerNotificationParserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleBrokerNotificationParserException(BrokerNotificationParserException ex,
                                                                            ServerHttpRequest request) {
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder()
                .title("BrokerNotificationParserException")
                .message(ex.getMessage())
                .path(path)
                .build());
    }

    @ExceptionHandler(HashCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleHashCreationException(HashCreationException ex, ServerHttpRequest request) {
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder()
                .title("HashCreationException")
                .message(ex.getMessage())
                .path(path)
                .build());
    }

    @ExceptionHandler(HashLinkException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleHashLinkException(HashLinkException ex, ServerHttpRequest request) {
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder()
                .title("HashLinkException")
                .message(ex.getMessage())
                .path(path)
                .build());
    }

    @ExceptionHandler(JsonReadingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<GlobalErrorMessage> handleJsonReadingException(JsonReadingException ex, ServerHttpRequest request) {
        String path = String.valueOf(request.getPath());
        return Mono.just(GlobalErrorMessage.builder()
                .title("JsonReadingException")
                .message(ex.getMessage())
                .path(path)
                .build());
    }

}