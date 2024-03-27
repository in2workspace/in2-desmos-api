package es.in2.desmos.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.api.exception.HashCreationException;
import es.in2.desmos.api.model.FailedEntityTransaction;
import es.in2.desmos.api.model.FailedEventTransaction;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.repository.FailedEntityTransactionRepository;
import es.in2.desmos.api.repository.FailedEventTransactionRepository;
import es.in2.desmos.api.repository.TransactionRepository;
import es.in2.desmos.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import static es.in2.desmos.api.util.ApplicationUtils.bytesAHex;
import static es.in2.desmos.api.util.ApplicationUtils.calculateIntertwinedHash;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final FailedEventTransactionRepository failedEventTransactionRepository;
    private final FailedEntityTransactionRepository failedEntityTransactionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> saveTransaction(String processId, Transaction transaction) {
        log.debug("ProcessID: {} - Saving transaction...", processId);
        // Create a Mono that either emits previousTransactionEntityHash or empty
        Mono<String> entityHashMono = (transaction.getEntityHash() == null || transaction.getEntityHash().isEmpty()) ?
                getEntityHashFromLastTransaction(processId, transaction.getEntityId())
                        .doOnNext(entityHash -> {
                            log.debug("ProcessID: {} - Previous transaction entity hash: {}", processId, entityHash);
                            transaction.setEntityHash(entityHash);
                        })
                        .switchIfEmpty(Mono.fromRunnable(() ->
                                log.debug("ProcessID: {} - No previous entity hash found", processId))) :
                Mono.empty(); // If entityHash is already set, emit empty to skip setting

        // Use then to chain saveTransactionWithHashLogic after entityHashMono
        return entityHashMono
                .then(saveTransactionWithHashLogic(processId, transaction));
    }

    private Mono<Void> saveTransactionWithHashLogic(String processId, Transaction transaction) {
        return getPreviousTransaction(processId).flatMap(
                previousTransaction -> {
                    log.debug("ProcessID: {} - Previous transaction: {}", processId, previousTransaction);
                    String currentTransactionHash = calculateTransactionHash(transaction);
                    log.debug("ProcessID: {} - Calculated hash: {}", processId, currentTransactionHash);
                    String concatenatedHash;
                    try {
                        concatenatedHash = calculateIntertwinedHash(currentTransactionHash, previousTransaction.getHash());
                    } catch (NoSuchAlgorithmException e) {
                        return Mono.error(new HashCreationException("Error calculating intertwined hash"));
                    }
                    transaction.setHash(concatenatedHash);
                    return transactionRepository.save(transaction);
                }
        ).switchIfEmpty(Mono.defer(() -> {
            String currentTransactionHash = calculateTransactionHash(transaction);
            transaction.setHash(currentTransactionHash);
            return transactionRepository.save(transaction);
        })).then();
    }

    public String calculateTransactionHash(Transaction transaction) {
        try {
            String json = objectMapper.writeValueAsString(transaction);

            byte[] bytes = json.getBytes();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);

            return bytesAHex(hash);
        } catch (Exception e) {
            throw new HashCreationException("Error while calculating hash");
        }
    }


    @Override
    public Mono<Void> saveFailedEventTransaction(String processId, FailedEventTransaction transaction) {
        log.debug("ProcessID: {} - Saving failed transaction...", processId);
        return failedEventTransactionRepository.save(transaction).doOnSuccess(success -> log.info("ProcessID: {} - Failed transaction saved successfully", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error saving failed transaction: {}", processId, error.getMessage()))
                .then();
    }

    @Override
    public Mono<Void> saveFailedEntityTransaction(String processId, FailedEntityTransaction transaction) {
        log.debug("ProcessID: {} - Saving failed entity transaction...", processId);
        return failedEntityTransactionRepository.save(transaction).doOnSuccess(success -> log.info("ProcessID: {} - Failed entity transaction saved successfully", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error saving failed entity transaction: {}", processId, error.getMessage()))
                .then();
    }

    @Override
    public Mono<Void> deleteFailedEntityTransaction(String processId, UUID transactionId) {
        log.debug("ProcessID: {} - Deleting failed entity transaction...", processId);
        return failedEntityTransactionRepository.deleteById(transactionId).doOnSuccess(success -> log.info("ProcessID: {} - Failed entity transaction deleted successfully", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error deleting failed entity transaction: {}", processId, error.getMessage()))
                .then();
    }

    @Override
    public Mono<Void> deleteFailedEventTransaction(String processId, UUID transactionId) {
        log.debug("ProcessID: {} - Deleting failed transaction...", processId);
        return failedEventTransactionRepository.deleteById(transactionId).doOnSuccess(success -> log.info("ProcessID: {} - Failed transaction deleted successfully", processId))
                .doOnError(error -> log.error("ProcessID: {} - Error deleting failed transaction: {}", processId, error.getMessage()))
                .then();
    }

    @Override
    public Flux<FailedEventTransaction> getAllFailedEventTransactions(String processId) {
        log.debug("ProcessID: {} - Getting all failed transactions", processId);
        return failedEventTransactionRepository.findAll();
    }

    @Override
    public Flux<FailedEntityTransaction> getAllFailedEntityTransactions(String processId) {
        log.debug("ProcessID: {} - Getting all failed entity transactions", processId);
        return failedEntityTransactionRepository.findAll();
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
    public Mono<Transaction> getPreviousTransaction(String processId) {
        log.debug("ProcessID: {} - Getting previous transaction...", processId);
        return transactionRepository.findPreviousTransaction();
    }

    @Override
    public Mono<Transaction> getLastProducerTransactionByEntityId(String processId, String entityId) {
        log.debug("ProcessID: {} - Getting last published producer transaction with id: {}", processId, entityId);
        return transactionRepository.findLastPublishedTransactionByEntityId(entityId).next();
    }

    @Override
    public Mono<String> getEntityHashFromLastTransaction(String processId, String entityId) {
        return transactionRepository.findLastTransactionByEntityId(entityId).next()
                .map(Transaction::getEntityHash);
    }

}
