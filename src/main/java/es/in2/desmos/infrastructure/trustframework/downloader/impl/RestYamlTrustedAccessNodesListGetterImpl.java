package es.in2.desmos.infrastructure.trustframework.downloader.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import es.in2.desmos.domain.models.TrustedAccessNodesList;
import es.in2.desmos.infrastructure.trustframework.downloader.RestTrustedAccessNodesListWebClient;
import es.in2.desmos.infrastructure.trustframework.downloader.RestYamlTrustedAccessNodesListGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestYamlTrustedAccessNodesListGetterImpl implements RestYamlTrustedAccessNodesListGetter {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final RestTrustedAccessNodesListWebClient restTrustedAccessNodesListWebClient;

    @Override
    public Mono<TrustedAccessNodesList> getTrustedAccessNodesList() {
        return restTrustedAccessNodesListWebClient
                .getAccessNodesListContent()
                .flatMap(this::deserializeYaml);
    }

    private Mono<TrustedAccessNodesList> deserializeYaml(String yamlContent) {
        try {
            return Mono.just(yamlMapper.readValue(yamlContent, TrustedAccessNodesList.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException(e));
        }
    }
}
