package es.in2.desmos.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * Configuration intended to be used by the OpenAPI documentation.
 *
 * @param server - server information
 * @param info   - organization information
 */
@ConfigurationProperties(prefix = "openapi")
public record OpenApiProperties(@NestedConfigurationProperty ServerProperties server,
                                @NestedConfigurationProperty InfoProperties info) {

    @ConstructorBinding
    public OpenApiProperties(ServerProperties server, InfoProperties info) {
        this.server = Optional.ofNullable(server).orElse(new ServerProperties(null, null));
        this.info = Optional.ofNullable(info).orElse(new InfoProperties(null, null, null, null, null, null));
    }

    public record ServerProperties(String url, String description) {
    }

    public record InfoProperties(String title, String version, String description, String termsOfService,
                                 @NestedConfigurationProperty ContactProperties contact,
                                 @NestedConfigurationProperty LicenseProperties license) {

        @ConstructorBinding
        public InfoProperties(String title, String version, String description, String termsOfService, ContactProperties contact, LicenseProperties license) {
            this.title = title;
            this.version = version;
            this.description = description;
            this.termsOfService = termsOfService;
            this.contact = Optional.ofNullable(contact).orElse(new ContactProperties(null, null, null));
            this.license = Optional.ofNullable(license).orElse(new LicenseProperties(null, null));
        }

        public record ContactProperties(String email, String name, String url) {
        }

        public record LicenseProperties(String name, String url) {
        }

    }

}
