package es.in2.desmos.infrastructure.trustframework.downloader;

import es.in2.desmos.domain.models.TrustedAccessNodesList;
import reactor.core.publisher.Mono;

public interface RestYamlTrustedAccessNodesListGetter {
    Mono<TrustedAccessNodesList> getTrustedAccessNodesList();
}
