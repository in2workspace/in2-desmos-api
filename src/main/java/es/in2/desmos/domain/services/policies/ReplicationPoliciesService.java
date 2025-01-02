package es.in2.desmos.domain.services.policies;

import es.in2.desmos.domain.models.MVEntityReplicationPoliciesInfo;
import reactor.core.publisher.Mono;

public interface ReplicationPoliciesService {
    Mono<Boolean> isMVEntityReplicable(String processId, MVEntityReplicationPoliciesInfo mvEntity);
}
