package es.in2.desmos.infrastructure.trustframework.cache;

import reactor.core.publisher.Mono;


public interface TrustedAccessNodesListCacheInitializator {

    Mono<Void> initialize(String processId);
}
