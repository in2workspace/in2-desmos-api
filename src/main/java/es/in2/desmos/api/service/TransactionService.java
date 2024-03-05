package es.in2.desmos.api.service;


import es.in2.desmos.api.model.FailedEntityTransaction;
import es.in2.desmos.api.model.FailedEventTransaction;
import es.in2.desmos.api.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    Mono<Void> saveTransaction(String processId, Transaction transaction);

    Mono<Void> saveFailedEventTransaction(String processId, FailedEventTransaction transaction);
    Flux<FailedEventTransaction> getAllFailedEventTransactions(String processId);

    Mono<Void> deleteFailedEventTransaction(String processId, UUID transactionId);

    Mono<Void> saveFailedEntityTransaction(String processId, FailedEntityTransaction transaction);

    Flux<FailedEntityTransaction> getAllFailedEntityTransactions(String processId);

    Mono<Void> deleteFailedEntityTransaction(String processId, UUID transactionId);


    Mono<List<Transaction>> getTransactionsByEntityId(String processId, String entityId);

    Flux<Transaction> getAllTransactions(String processId);

    Mono<Transaction> findLatestPublishedOrDeletedTransactionForEntity(String processId, String entityId);

    Mono<Transaction> getLastProducerTransaction(String processIdd);

    Mono<Transaction> getLastProducerTransactionByEntityId(String processId, String entityId);


}
