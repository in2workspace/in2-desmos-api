package es.in2.desmos.api.config.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration of the client which instantiate the solution.
 *
 * @param organizationId - Client OrganizationID information
 */
@Slf4j
@ConfigurationProperties(prefix = "client")
public record ClientProperties(String organizationId) {
}
