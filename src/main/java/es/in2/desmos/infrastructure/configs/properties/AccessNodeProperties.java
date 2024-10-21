package es.in2.desmos.infrastructure.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Access Node Properties
 *
 * @param trustedAccessNodesList - endpoint to notify on events
 */
@ConfigurationProperties(prefix = "access-node")
public record AccessNodeProperties(String trustedAccessNodesList) {
}
