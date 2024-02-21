package es.in2.desmos.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration of the client which instantiate the solution.
 *
 * @param organizationId - Client OrganizationID information
 */
@ConfigurationProperties(prefix = "client")
public record ClientProperties(String organizationId) {
}
