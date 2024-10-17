package es.in2.desmos.infrastructure.trustframework.cache;

import es.in2.desmos.infrastructure.configs.properties.AccessNodeProperties;
import es.in2.desmos.infrastructure.trustframework.downloader.RestYamlTrustedAccessNodesListGetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrustedAccessNodesListCacheInitializatorImpl implements TrustedAccessNodesListCacheInitializator {
    private final AccessNodeProperties accessNodeProperties;
    private final TrustedAccessNodesListCache trustedAccessNodesListCache;
    private final RestYamlTrustedAccessNodesListGetter restYamlTrustedAccessNodesListGetter;

    @Override
    public Mono<Void> initialize(String processId) {

        log.debug("ProcessID: {} - Retrieving YAML data from the external source repository...", processId);
        // Get the External URL from configuration
        String repoPath = accessNodeProperties.trustedAccessNodesList();

        log.debug("ProcessID: {} - External URL: {}", processId, repoPath);
        // Retrieve YAML data from the External URL

        return restYamlTrustedAccessNodesListGetter
                .getTrustedAccessNodesList()
                .flatMap(accessNodesList -> {
                    trustedAccessNodesListCache.save(Mono.just(accessNodesList));
                    return Mono.empty();
                });
    }
}
