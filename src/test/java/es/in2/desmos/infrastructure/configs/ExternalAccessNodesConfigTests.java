package es.in2.desmos.infrastructure.configs;

import es.in2.desmos.infrastructure.configs.properties.ExternalAccessNodesProperties;
import es.in2.desmos.objectmothers.UrlMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalAccessNodesConfigTests {

    @InjectMocks
    ExternalAccessNodesConfig externalAccessNodesConfig;

    @Mock
    ExternalAccessNodesProperties externalAccessNodesProperties;

    @Test
    void itShouldReturnExternalAccessNodesUrls() {

        when(externalAccessNodesProperties.urls()).thenReturn(UrlMother.commaSeparatedExample1And2Urls());

        Mono<List<String>> result = externalAccessNodesConfig.getExternalAccessNodesUrls();

        StepVerifier
                .create(result)
                .expectNext(UrlMother.example1And2urlsList())
                .verifyComplete();
    }
}