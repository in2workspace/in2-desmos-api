package es.in2.desmos.configs;

import es.in2.desmos.configs.properties.ExternalAccessNodesProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExternalAccessNodesConfig {
    private final ExternalAccessNodesProperties externalAccessNodesProperties;

    public Mono<List<String>> getExternalAccessNodesUrls() {
        String externalAccessNodes = externalAccessNodesProperties.urls();
        return getUrlsListFromCommaSeparatedString(externalAccessNodes);
    }

    private Mono<List<String>> getUrlsListFromCommaSeparatedString(String commaSeparatedUrlsList) {
        String commaSeparatedUrlsListWithoutSpaces = removeSpaces(commaSeparatedUrlsList);

        return Mono.just(Arrays.asList(commaSeparatedUrlsListWithoutSpaces.split(",")));
    }

    private static String removeSpaces(String text) {
        return text.replaceAll("\\s+", "");
    }

}
