package es.in2.desmos.domain.repositories;

import es.in2.desmos.domain.models.BlockchainNotificationRecover;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscribeWorkflowRecoverRepository extends ReactiveCrudRepository<BlockchainNotificationRecover, UUID> {
}
