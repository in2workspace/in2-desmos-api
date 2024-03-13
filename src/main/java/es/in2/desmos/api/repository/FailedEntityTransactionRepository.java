package es.in2.desmos.api.repository;

import es.in2.desmos.api.model.FailedEntityTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FailedEntityTransactionRepository extends ReactiveCrudRepository<FailedEntityTransaction, UUID> {
}
