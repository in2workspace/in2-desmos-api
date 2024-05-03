package es.in2.desmos.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("external-access-nodes")
public record ExternalAccessNodesProperties(String urls) {
}