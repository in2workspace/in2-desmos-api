package es.in2.desmos.blockchain.config.properties;

/**
 * EVM Adapter Path Properties
 *
 * @param nodeConfiguration - configure node path
 * @param publication       - publish path
 * @param subscription      - subscribe path
 * @param events            - events path
 */
public record BlockchainAdapterPathProperties(String nodeConfiguration, String publication, String subscription, String events) {
}
