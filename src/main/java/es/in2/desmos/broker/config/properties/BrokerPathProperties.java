package es.in2.desmos.broker.config.properties;

/**
 * NGSI-LD Paths
 *
 * @param entities      - entities path
 * @param subscriptions - subscriptions path
 * @param temporal      - temporal path
 */
public record BrokerPathProperties(String entities, String subscriptions, String temporal) {
}
