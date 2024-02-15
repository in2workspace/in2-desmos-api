package es.in2.desmos.api.config.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * OpenApiInfoProperties
 *
 * @param title          - title
 * @param version        - version
 * @param description    - description
 * @param termsOfService - terms of service
 * @param contact        - contact information
 * @param license        - project license
 */
public record OpenApiInfoProperties(String title, String version, String description, String termsOfService,
                                    @NestedConfigurationProperty OpenApiInfoContactProperties contact,
                                    @NestedConfigurationProperty OpenApiInfoLicenseProperties license) {

    @ConstructorBinding
    public OpenApiInfoProperties(String title, String version, String description, String termsOfService,
                                 OpenApiInfoContactProperties contact, OpenApiInfoLicenseProperties license) {
        this.title = title;
        this.version = version;
        this.description = description;
        this.termsOfService = termsOfService;
        this.contact = Optional.ofNullable(contact).orElse(new OpenApiInfoContactProperties(null, null, null));
        this.license = Optional.ofNullable(license).orElse(new OpenApiInfoLicenseProperties(null, null));
    }

}
