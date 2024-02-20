package es.in2.desmos.api.service;


import es.in2.desmos.api.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransactionService {

    Mono<Void> saveTransaction(String processId, Transaction transaction);

    Mono<List<Transaction>> getTransactionsByEntityId(String processId, String entityId);

    Flux<Transaction> getAllTransactions(String processId);

    Mono<Transaction> findLatestPublishedOrDeletedTransactionForEntity(String processId, String entityId);

    Mono<Transaction> getLastProducerTransaction(String processIdd);

    Mono<Transaction> getLastProducerTransactionByEntityId(String processId, String entityId);

}
