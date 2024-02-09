package es.in2.desmos.blockchain.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * EVM Adapter Properties
 *
 * @param provider       - provider of the EVM adapter
 * @param internalDomain - internal domain
 * @param externalDomain - external domain
 * @param paths          - paths
 */
@ConfigurationProperties(prefix = "blockchain-adapter")
public record BlockchainAdapterProperties(String provider, String internalDomain, String externalDomain,
                                          @NestedConfigurationProperty BlockchainAdapterPathProperties paths) {

    @ConstructorBinding
    public BlockchainAdapterProperties(String provider, String internalDomain, String externalDomain, BlockchainAdapterPathProperties paths) {
        this.provider = provider;
        this.internalDomain = internalDomain;
        this.externalDomain = externalDomain;
        this.paths = Optional.ofNullable(paths).orElse(new BlockchainAdapterPathProperties(null, null, null));
    }

}
