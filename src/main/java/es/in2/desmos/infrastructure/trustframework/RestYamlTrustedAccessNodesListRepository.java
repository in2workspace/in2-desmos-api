package es.in2.desmos.infrastructure.trustframework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import es.in2.desmos.domain.models.AccessNodeYamlData;
import es.in2.desmos.domain.repositories.TrustedAccessNodesListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestYamlTrustedAccessNodesListRepository implements TrustedAccessNodesListRepository {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final RestTrustedAccessNodesListWebClient restTrustedAccessNodesListWebClient;

    @Override
    public Mono<Boolean> existsDltAddressByValue(Mono<String> dltAddress) {
        return restTrustedAccessNodesListWebClient
                .getAccessNodesListContent()
                .flatMap(this::deserializeYaml)
                .flatMap(accessNodeYamlData ->
                        dltAddress.flatMap(address -> {
                            boolean exists = accessNodeYamlData
                                    .getOrganizations()
                                    .stream()
                                    .anyMatch(organization -> organization.getDltAddress().equals(address));
                            return Mono.just(exists);
                        })
                );
    }

    private Mono<AccessNodeYamlData> deserializeYaml(String yamlContent) {
        try {
            return Mono.just(yamlMapper.readValue(yamlContent, AccessNodeYamlData.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException(e));
        }
    }
}
