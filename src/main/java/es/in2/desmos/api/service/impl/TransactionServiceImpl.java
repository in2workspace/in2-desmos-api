package es.in2.desmos.api.service.impl;

import es.in2.desmos.api.exception.HashCreationException;
import es.in2.desmos.api.exception.HashLinkException;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.repository.TransactionRepository;
import es.in2.desmos.api.service.TransactionService;
import es.in2.desmos.broker.config.properties.BrokerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static es.in2.desmos.api.util.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BrokerProperties brokerProperties;

    @Override
    public Mono<Void> saveTransaction(String processId, Transaction transaction) {
        log.debug("ProcessID: {} - Saving transaction...", processId);
        try {
            if (transaction.getTrader() == TransactionTrader.PRODUCER && hasHlParameter(transaction.getDatalocation())) {
                // In case of a producer transaction, we calculate the intertwined hash and save it
                Mono<String> entityHashMono = getLastProducerTransactionByEntityId(processId, transaction.getEntityId())
                        .flatMap(lastTransaction -> getEntityHashFromLastTransaction(processId, transaction.getEntityId()))
                        .switchIfEmpty(Mono.just(transaction.getEntityHash()));

                return calculateTransactionIntertwinedHash(transaction.getEntityHash(), processId)
                        .flatMap(intertwinedHash -> entityHashMono.flatMap(entityHash -> {
                            transaction.setHash(intertwinedHash);
                            transaction.setEntityHash(entityHash);
                            return transactionRepository.save(transaction)
                                    .doOnSuccess(success -> log.info("ProcessID: {} - Transaction saved successfully", processId))
                                    .doOnError(error -> log.error("ProcessID: {} - Error saving producer transaction: {}", processId, error.getMessage()));
                        })).then();
            } else {
                // In case of a consumer transaction, we save it without calculating the intertwined hash
                return transactionRepository.save(transaction)
                        .doOnSuccess(success -> log.info("ProcessID: {} - Transaction saved successfully", processId))
                        .doOnError(error -> log.error("ProcessID: {} - Error saving consumer transaction: {}", processId, error.getMessage()))
                        .then();
            }
        } catch (HashLinkException e) {
            // In case of an error while calculating the intertwined hash, it means that the transaction is a deleted one
            return transactionRepository.save(transaction)
                    .doOnSuccess(success -> log.info("ProcessID: {} - Deletion transaction saved successfully", processId))
                    .doOnError(error -> log.error("ProcessID: {} - Error saving deleted transaction: {}", processId, error.getMessage()))
                    .then();
        }
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

    @Override
    public Mono<Transaction> getLastProducerTransaction(String processId) {
        log.debug("ProcessID: {} - Getting last published producer transaction...", processId);
        return transactionRepository.findLastProducerTransaction().next();
    }

    @Override
    public Mono<Transaction> getLastProducerTransactionByEntityId(String processId, String entityId) {
        log.debug("ProcessID: {} - Getting last published producer transaction with id: {}", processId, entityId);
        return transactionRepository.findLastProducerTransactionByEntityId(entityId).next();
    }

    private Mono<String> calculateTransactionIntertwinedHash(String entityHash, String processId) {
        return getLastProducerTransaction(processId)
                .flatMap(transaction -> Mono.fromCallable(() -> {
                    try {
                        return calculateIntertwinedHash(entityHash, transaction.getHash());
                    } catch (NoSuchAlgorithmException e) {
                        throw new HashCreationException("Error while calculating intertwined hash", e);
                    }
                })).switchIfEmpty(Mono.just(entityHash));
    }

    private Mono<String> getEntityHashFromLastTransaction(String processId, String entityId) {
        return getLastProducerTransactionByEntityId(processId, entityId)
                .map(Transaction::getEntityHash);
    }


}
