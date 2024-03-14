package es.in2.desmos.blockchain.config.properties;

/**
 * EVM Adapter Path Properties
 *
 * @param publication       - publish path
 * @param subscription      - subscribe path
 * @param events            - events path
 */
public record DLTAdapterPathProperties(String publication, String subscription, String events) {

}
