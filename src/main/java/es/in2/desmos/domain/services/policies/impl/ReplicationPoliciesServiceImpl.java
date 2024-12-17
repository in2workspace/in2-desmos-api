package es.in2.desmos.domain.services.policies.impl;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.models.ReplicationPolicies;
import es.in2.desmos.domain.services.policies.ReplicationPoliciesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplicationPoliciesServiceImpl implements ReplicationPoliciesService {

    private static final Set<String> VALID_STATUSES = Set.of("Launched", "Retired", "Obsolete");

    @Override
    public Mono<Boolean> isMVEntityReplicable(String processId, MVEntity4DataNegotiation mvEntity) {
        boolean isLifecycleStatusReplicable = isLifecycleStatusReplicable(mvEntity.lifecycleStatus());
        if (!isLifecycleStatusReplicable) {
            log.warn("ProcessID: {} - Global policies validation failed for Policy '{}' in Entity with ID '{}'. " +
                            "Reason: lifecycleStatus '{}' is not replicable.",
                    processId, ReplicationPolicies.GP_1, mvEntity.id(), mvEntity.lifecycleStatus());

        }

        boolean isValidForReplicable = isValidForReplicable(mvEntity.startDateTime(), mvEntity.endDateTime());
        if (!isValidForReplicable) {
            log.warn("ProcessID: {} - Global policies validation failed for Policy '{}' in Entity with ID '{}'. " +
                            "Reason: validFor with startDateTime '{}' and endDateTime '{}' is not replicable.",
                    processId, ReplicationPolicies.GP_2, mvEntity.id(), mvEntity.startDateTime(), mvEntity.endDateTime());

        }

        if (isLifecycleStatusReplicable &&
                isValidForReplicable) {
            log.debug("ProcessID: {} - Global policies validation successfully in Entity with ID {}.",
                    processId, mvEntity.id());
            return Mono.just(true);
        } else {
            return Mono.just(false);
        }
    }

    private boolean isLifecycleStatusReplicable(String lifecycleStatus) {
        return VALID_STATUSES.contains(lifecycleStatus);
    }

    private boolean isValidForReplicable(String startDateTime, String endDateTime) {
        return isAfterStartDateTime(startDateTime) && isBeforeEndDateTime(endDateTime);
    }

    private boolean isAfterStartDateTime(String startDateTime) {
        return startDateTime == null ||
                Instant.now().isAfter(Instant.parse(startDateTime));
    }

    private boolean isBeforeEndDateTime(String endDateTime) {
        return endDateTime == null ||
                Instant.now().isBefore(Instant.parse(endDateTime));
    }
}