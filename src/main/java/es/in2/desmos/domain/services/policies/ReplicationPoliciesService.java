package es.in2.desmos.domain.services.policies;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Mono;

public interface ReplicationPoliciesService {
    Mono<Boolean> isMVEntityReplicable(String processId, MVEntity4DataNegotiation mvEntity);
}
