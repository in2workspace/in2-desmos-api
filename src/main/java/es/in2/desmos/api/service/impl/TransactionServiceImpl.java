package es.in2.desmos.api.service.impl;

import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.repository.TransactionRepository;
import es.in2.desmos.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Mono<Void> saveTransaction(String processId, Transaction transaction) {
        return transactionRepository.save(transaction)
                .doOnSuccess(success -> log.info("ProcessID: {} - Transaction saved successfully", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error saving transaction: {}", processId, error.getMessage()))
                .then();
    }

    @Override
    public Mono<List<Transaction>> getTransactionsByEntityId(String processId, String entityId) {
        log.debug("ProcessID: {} - Getting transactions with id: {}", processId, entityId);
        return transactionRepository.findByEntityId(entityId)
                .collectList();
    }

    @Override
    public Flux<Transaction> getAllTransactions(String processId) {
        log.debug("ProcessID: {} - Getting all transactions", processId);
        return transactionRepository.findAll();
    }

    @Override
    public Mono<Transaction> findLatestPublishedOrDeletedTransactionForEntity(String processId, String entityId) {
        log.debug("ProcessID: {} - Getting latest published or deleted transaction for entity: {}", processId, entityId);
        return transactionRepository.findLatestByEntityIdAndStatusPublishedOrDeleted(entityId);
    }

}
