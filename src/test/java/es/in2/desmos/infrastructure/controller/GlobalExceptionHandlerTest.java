package es.in2.desmos.infrastructure.controller;

import es.in2.desmos.domain.exception.*;
import es.in2.desmos.domain.model.GlobalErrorMessage;
import es.in2.desmos.infrastructure.controller.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static GlobalExceptionHandler globalExceptionHandler;
    private ServerHttpRequest request;
    private RequestPath requestPath;

    static Stream<Arguments> provideData() {
        List<Class<?>> classes = new ArrayList<>(Arrays.asList(
                SubscriptionCreationException.class,
                BrokerNotificationParserException.class,
                HashCreationException.class,
                HashLinkException.class,
                JsonReadingException.class
        ));

        List<String> messages = new ArrayList<>(Arrays.asList(
                "SubscriptionCreationException",
                "BrokerNotificationParser",
                "HashCreation",
                "HashLink",
                "JsonReading"
        ));
        List<Throwable> causesNull = new ArrayList<>();
        List<Throwable> causesException = new ArrayList<>();
        for (int i = 0; i < classes.size() / 2; i++) {
            causesNull.add(null);
            causesException.add(new RuntimeException("cause"));
        }
        List<Throwable> causes = new ArrayList<>();
        for (int i = 0; i < classes.size() / 2; i++) {
            causes.add(causesNull.get(i));
            causes.add(causesException.get(i));
        }
        List<BiFunction<RuntimeException, ServerHttpRequest, Mono<GlobalErrorMessage>>> methods = new ArrayList<>(Arrays.asList(
                (ex, req) -> globalExceptionHandler.handleBlockchainNodeSubscriptionException((SubscriptionCreationException) ex, req),
                (ex, req) -> globalExceptionHandler.handleBrokerNotificationParserException((BrokerNotificationParserException) ex, req),
                (ex, req) -> globalExceptionHandler.handleHashCreationException((HashCreationException) ex, req),
                (ex, req) -> globalExceptionHandler.handleHashLinkException((HashLinkException) ex, req),
                (ex, req) -> globalExceptionHandler.handleJsonReadingException((JsonReadingException) ex, req)
        ));
        classes.addAll(new ArrayList<>(classes));
        messages.addAll(new ArrayList<>(messages));
        methods.addAll(new ArrayList<>(methods));
        return IntStream.range(0, classes.size())
                .mapToObj(i -> Arguments.of(classes.get(i), messages.get(i), causes.get(i % causes.size()), methods.get(i)));
    }

    @BeforeEach
    void setup() {
        request = mock(ServerHttpRequest.class);
        requestPath = mock(RequestPath.class);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @ParameterizedTest
    @MethodSource("provideData")
    void testExceptions(Class<?> exceptionClass, String message, Throwable cause,
                        BiFunction<RuntimeException, ServerHttpRequest, Mono<GlobalErrorMessage>> method) {
        // Mock
        when(request.getPath()).thenReturn(requestPath);
        // Act
        Object exception;
        try {
            if (cause == null) {
                exception = exceptionClass.getConstructor(String.class)
                        .newInstance(message);
            } else {
                exception = exceptionClass.getConstructor(String.class, Throwable.class)
                        .newInstance(message, cause);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating exception", e);
        }
        GlobalErrorMessage globalErrorMessage =
                GlobalErrorMessage.builder()
                        .title(exceptionClass.getSimpleName())
                        .message(message)
                        .path(String.valueOf(requestPath))
                        .build();
        //Assert
        StepVerifier.create(method.apply((RuntimeException) exception, request))
                .expectNext(globalErrorMessage)
                .verifyComplete();
    }

}