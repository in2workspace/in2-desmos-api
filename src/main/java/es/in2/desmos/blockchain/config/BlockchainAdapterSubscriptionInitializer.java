package es.in2.desmos.blockchain.config;

import es.in2.desmos.api.config.ApplicationConfig;
import es.in2.desmos.blockchain.config.properties.BlockchainNodeProperties;
import es.in2.desmos.blockchain.config.properties.EventSubscriptionProperties;
import es.in2.desmos.blockchain.model.BlockchainNode;
import es.in2.desmos.blockchain.model.BlockchainAdapterSubscription;
import es.in2.desmos.blockchain.service.BlockchainAdapterNodeService;
import es.in2.desmos.blockchain.service.BlockchainAdapterSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlockchainAdapterSubscriptionInitializer {

    private final EventSubscriptionProperties eventSubscriptionProperties;
    private final BlockchainNodeProperties blockchainNodeProperties;
    private final BlockchainAdapterNodeService blockchainAdapterNodeService;
    private final BlockchainAdapterSubscriptionService blockchainAdapterSubscriptionService;
    private final ApplicationConfig applicationConfig;

    @EventListener(ApplicationReadyEvent.class)
    public Mono<Void> initializeSubscriptions() {
        String processId = UUID.randomUUID().toString();
        return setBlockchainNodeConnection(processId)
                .flatMap(response -> setBlockchainEventSubscription(processId));
    }

    private Mono<String> setBlockchainNodeConnection(String processId) {
        log.info("Setting Blockchain Node connection...");
        // Create the Blockchain Node object
        BlockchainNode blockchainNode = BlockchainNode.builder()
                .rpcAddress(blockchainNodeProperties.rpcAddress())
                .userEthereumAddress(blockchainNodeProperties.userEthereumAddress())
                .organizationId(applicationConfig.organizationIdHash())
                .build();
        // Create the subscription
        return blockchainAdapterNodeService.createBlockchainNodeConnection(processId, blockchainNode)
                .doOnSuccess(response -> log.info("Blockchain Node connection created successfully"))
                .doOnError(e -> log.error("Error creating Blockchain Node connection", e));
    }

    private Mono<Void> setBlockchainEventSubscription(String processId) {
        log.info("Setting Blockchain Event Subscription...");
        // Create the EVM Subscription object
        BlockchainAdapterSubscription blockchainAdapterSubscription = BlockchainAdapterSubscription.builder()
                .eventTypes(eventSubscriptionProperties.eventTypes())
                .notificationEndpoint(eventSubscriptionProperties.notificationEndpoint())
                .build();
        // Create the subscription
        return blockchainAdapterSubscriptionService.createSubscription(processId, blockchainAdapterSubscription)
                .doOnSuccess(response -> log.info("Blockchain Event Subscription created successfully"))
                .doOnError(e -> log.error("Error creating Blockchain Event Subscription", e));
    }

}
