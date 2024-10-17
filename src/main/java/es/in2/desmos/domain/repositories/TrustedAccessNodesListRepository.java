package es.in2.desmos.domain.repositories;

import es.in2.desmos.domain.models.TrustedAccessNodesList;
import reactor.core.publisher.Mono;

public interface TrustedAccessNodesListRepository {
    Mono<Boolean> existsDltAddressByValue(Mono<String> dltAddress);

    Mono<TrustedAccessNodesList> getTrustedAccessNodeList();
}
