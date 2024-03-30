//package es.in2.desmos.domain.repository;
//
//
//import es.in2.desmos.domain.model.Transaction;
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.UUID;
//
//@Repository
//public interface TransactionRepository extends ReactiveCrudRepository<Transaction, UUID> {
//
//    Flux<Transaction> findByEntityId(final String entityId);
//
//    @Query("SELECT * FROM transactions WHERE entity_id = :entityId AND status = 'PUBLISHED' OR status = 'DELETED' ORDER BY created_at DESC LIMIT 1")
//    Mono<Transaction> findLatestByEntityIdAndStatusPublishedOrDeleted(String entityId);
//
//    @Query("SELECT * FROM transactions WHERE status = 'PUBLISHED' AND trader = 'PRODUCER' ORDER BY created_at DESC LIMIT 1")
//    Flux<Transaction> findLastProducerTransaction();
//
//    @Query("SELECT * FROM transactions ORDER BY created_at DESC LIMIT 1 OFFSET 1")
//    Mono<Transaction> findPreviousTransaction();
//
//    @Query("SELECT * FROM transactions WHERE entity_id = :entityId AND status = 'PUBLISHED' AND trader = 'PRODUCER' ORDER BY created_at DESC LIMIT 1")
//    Flux<Transaction> findLastPublishedTransactionByEntityId(String entityId);
//
//    @Query("SELECT * FROM transactions WHERE entity_id = :entityId ORDER BY created_at DESC LIMIT 1")
//    Flux<Transaction> findLastTransactionByEntityId(String entityId);
//
//
//
//}
