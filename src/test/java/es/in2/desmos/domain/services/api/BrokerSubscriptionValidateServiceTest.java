package es.in2.desmos.domain.services.api;

import es.in2.desmos.domain.services.api.impl.BrokerSubscriptionValidateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BrokerSubscriptionValidateServiceTest {


    @InjectMocks
    BrokerSubscriptionValidateServiceImpl brokerSubscriptionValidateService;

    @Test
    void itShouldValidateCorrectSubscriptionId(){

        String processId = "0";
        String subscriptionId = "jfldsajlfdsaijfodsj";

        brokerSubscriptionValidateService.setSubscriptionId(processId, subscriptionId).block();

        var result = brokerSubscriptionValidateService.validateSubscription(processId, subscriptionId);

        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void itShouldValidateIncorrectSubscriptionId(){

        String processId = "0";

        brokerSubscriptionValidateService.setSubscriptionId(processId, "jfldsajlfdsaijfodsj").block();

        var result = brokerSubscriptionValidateService.validateSubscription(processId, "other");

        StepVerifier
                .create(result)
                .expectError()
                .verify();


    }

}