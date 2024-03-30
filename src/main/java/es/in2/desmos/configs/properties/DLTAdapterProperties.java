package es.in2.desmos.configs.properties;

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
@ConfigurationProperties(prefix = "dlt-adapter")
public record DLTAdapterProperties(String provider, String internalDomain, String externalDomain,
                                   @NestedConfigurationProperty DLTAdapterPathProperties paths) {

    @ConstructorBinding
    public DLTAdapterProperties(String provider, String internalDomain, String externalDomain, DLTAdapterPathProperties paths) {
        this.provider = provider;
        this.internalDomain = internalDomain;
        this.externalDomain = externalDomain;
        this.paths = Optional.ofNullable(paths).orElse(new DLTAdapterPathProperties(null, null, null));
    }

    public record DLTAdapterPathProperties(String publication, String subscription, String events) {}

}
