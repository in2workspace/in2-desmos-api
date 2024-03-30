package es.in2.desmos.configs;

import es.in2.desmos.configs.properties.BrokerProperties;
import es.in2.desmos.configs.properties.OrganizationProperties;
import es.in2.desmos.configs.properties.DLTAdapterProperties;
import es.in2.desmos.configs.properties.OpenApiProperties;
import es.in2.desmos.domain.exceptions.HashCreationException;
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

import static es.in2.desmos.domain.utils.ApplicationUtils.calculateSHA256;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApiConfig {

    private final OpenApiProperties openApiProperties;
    private final OrganizationProperties organizationProperties;

    @Bean
    public String organizationIdHash() {
        try {
            return calculateSHA256(organizationProperties.organizationId());
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

}
