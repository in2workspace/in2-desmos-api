package es.in2.desmos.blockchain.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration to connect the blockchain
 *
 * @param provider            - provider of the blockchain
 * @param rpcAddress          - rpc address of the node to be used
 * @param userEthereumAddress - address of the user in the ethereum compatible blockchain
 */
@ConfigurationProperties(prefix = "blockchain")
public record BlockchainNodeProperties(String provider, String rpcAddress, String userEthereumAddress) {
}
