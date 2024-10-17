package es.in2.desmos.infrastructure.trustframework.downloader;

import reactor.core.publisher.Mono;

public interface RestTrustedAccessNodesListWebClient {
    Mono<String> getAccessNodesListContent();
}
