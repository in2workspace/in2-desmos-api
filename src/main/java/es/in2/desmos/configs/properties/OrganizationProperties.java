package es.in2.desmos.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration of the organization ID which instantiates the solution.
 *
 * @param organizationId - OrganizationID information
 */
@ConfigurationProperties(prefix = "organization")
public record OrganizationProperties(String organizationId) {
}
