package es.in2.desmos.domain.repositories;

import reactor.core.publisher.Mono;

public interface TrustedAccessNodesListRepository {
    Mono<Boolean> existsDltAddressByValue(Mono<String> dltAddress);
}
