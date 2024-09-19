package es.in2.desmos.infrastructure.configs;

import es.in2.desmos.infrastructure.configs.properties.ExternalAccessNodesProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExternalAccessNodesConfig {
    private final ExternalAccessNodesProperties externalAccessNodesProperties;

    public Mono<List<String>> getExternalAccessNodesUrls() {
        String externalAccessNodes = externalAccessNodesProperties.urls();
        if(externalAccessNodes != null && !externalAccessNodes.isBlank()){
            return getUrlsListFromCommaSeparatedString(externalAccessNodes);
        } else{
            return Mono.just(new ArrayList<>());
        }
    }

    private Mono<List<String>> getUrlsListFromCommaSeparatedString(String commaSeparatedUrlsList) {
        String commaSeparatedUrlsListWithoutSpaces = removeSpaces(commaSeparatedUrlsList);

        return Mono.just(Arrays.asList(commaSeparatedUrlsListWithoutSpaces.split(",")));
    }

    private String removeSpaces(String text) {
        return text.replaceAll("\\s+", "");
    }

}
