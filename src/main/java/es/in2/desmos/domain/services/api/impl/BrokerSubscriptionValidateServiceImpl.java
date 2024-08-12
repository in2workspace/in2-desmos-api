package es.in2.desmos.domain.services.api.impl;

import es.in2.desmos.domain.exceptions.UnauthorizedBrokerSubscriptionException;
import es.in2.desmos.domain.services.api.BrokerSubscriptionValidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerSubscriptionValidateServiceImpl implements BrokerSubscriptionValidateService {

    private String subscriptionId;

    @Override
    public Mono<Void> setSubscriptionId(String processId, String id) {
        return Mono.fromRunnable(() -> {
            subscriptionId = id;
            log.info("ProcessID: {} - Broker Subscription Id set successfully.", processId);
        });
    }

    @Override
    public Mono<Void> validateSubscription(String processId, String idToValidate) {
        return Objects.equals(idToValidate, subscriptionId)
                ? Mono.empty()
                : Mono.error(new UnauthorizedBrokerSubscriptionException("Broker Subscription not found"));
    }
}
