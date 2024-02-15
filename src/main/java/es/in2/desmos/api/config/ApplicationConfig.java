package es.in2.desmos.api.config;

import es.in2.desmos.api.config.properties.ClientProperties;
import es.in2.desmos.api.config.properties.OpenApiProperties;
import es.in2.desmos.api.exception.HashCreationException;
import es.in2.desmos.api.service.QueueService;
import es.in2.desmos.api.service.impl.QueueServiceImpl;
import es.in2.desmos.blockchain.config.properties.BlockchainAdapterProperties;
import es.in2.desmos.broker.config.properties.BrokerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static es.in2.desmos.api.util.ApplicationUtils.calculateSHA256Hash;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final OpenApiProperties openApiProperties;
    private final ClientProperties clientProperties;
    private final BlockchainAdapterProperties blockchainAdapterProperties;
    private final BrokerProperties brokerProperties;

    @PostConstruct
    public void init() {
        log.debug("OpenApi properties: {}", openApiProperties);
        log.debug("Operator properties: {}", clientProperties);
        log.debug("DLT adapter properties: {}", blockchainAdapterProperties);
        log.debug("Broker properties: {}", brokerProperties);
    }

    @Bean
    public String organizationIdHash() {
        try {
            return calculateSHA256Hash(clientProperties.organizationId());
        } catch (NoSuchAlgorithmException e) {
            throw new HashCreationException("Error creating organizationId hash: " + e.getMessage());
        }
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .contact(new Contact()
                                .email(openApiProperties.info().contact().email())
                                .name(openApiProperties.info().contact().name())
                                .url(openApiProperties.info().contact().url()))
                        .license(new License()
                                .name(openApiProperties.info().license().name())
                                .url(openApiProperties.info().license().url())))
                .servers(List.of(new Server()
                        .url(openApiProperties.server().url())
                        .description(openApiProperties.server().description())));
    }

    @Bean
    public QueueService brokerToBlockchainQueueService() {
        return new QueueServiceImpl();
    }

    @Bean
    public QueueService blockchainToBrokerQueueService() {
        return new QueueServiceImpl();
    }

}
