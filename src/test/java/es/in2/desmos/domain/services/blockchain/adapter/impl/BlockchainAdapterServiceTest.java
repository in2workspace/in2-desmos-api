package es.in2.desmos.domain.services.blockchain.adapter.impl;

import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.it.ContainerManager;
import es.in2.desmos.objectmothers.BlockchainSubscriptionMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class BlockchainAdapterServiceTest {

    @Autowired
    private BlockchainAdapterService blockchainAdapterService;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Test
    void itShouldGetSubscriptions() {

        BlockchainSubscription expected1 = BlockchainSubscriptionMother.defaultConfigured();
        BlockchainSubscription expected2 = BlockchainSubscriptionMother.sample();
        BlockchainSubscription expected3 = BlockchainSubscriptionMother.otherEventTypesSubscription();

        createSubscriptions(expected2, expected3);

        var resultFlux = blockchainAdapterService.getSubscriptions("0");

        StepVerifier
                .create(resultFlux)
                .assertNext(result -> assertThat(result).isEqualTo(expected1))
                .assertNext(result -> assertThat(result).isEqualTo(expected2))
                .assertNext(result -> assertThat(result).isEqualTo(expected3))
                .verifyComplete();
    }

    private void createSubscriptions(BlockchainSubscription... blockchainSubscriptions) {
        for (var subscription : blockchainSubscriptions) {
            blockchainAdapterService.createSubscription("0", subscription).block();
        }
    }
}