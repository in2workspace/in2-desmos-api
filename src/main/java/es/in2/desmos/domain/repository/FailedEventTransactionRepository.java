package es.in2.desmos.domain.repository;

import es.in2.desmos.domain.model.FailedEventTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FailedEventTransactionRepository extends ReactiveCrudRepository<FailedEventTransaction, UUID> {
}
