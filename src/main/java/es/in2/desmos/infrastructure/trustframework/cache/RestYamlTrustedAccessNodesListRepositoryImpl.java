package es.in2.desmos.infrastructure.trustframework.cache;

import es.in2.desmos.domain.models.TrustedAccessNodesList;
import es.in2.desmos.domain.repositories.TrustedAccessNodesListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestYamlTrustedAccessNodesListRepositoryImpl implements TrustedAccessNodesListRepository {

    private final TrustedAccessNodesListCache trustedAccessNodesListCache;

    @Override
    public Mono<Boolean> existsDltAddressByValue(Mono<String> dltAddress) {
        return dltAddress.flatMap(address ->
                trustedAccessNodesListCache.find()
                        .map(trustedAccessNodesList -> {
                            // Check if any organization has the given DLT address
                            return trustedAccessNodesList
                                    .getOrganizations()
                                    .stream()
                                    .anyMatch(organization -> organization.getDltAddress().equals(address));
                        })
        );
//        return Mono.just(Boolean.FALSE);
    }

    @Override
    public Mono<TrustedAccessNodesList> getTrustedAccessNodeList() {
        return trustedAccessNodesListCache.find();
//        return Mono.just(new TrustedAccessNodesList());
    }
}
