package es.in2.desmos.api.config.properties;

/**
 * OpenApiInfoContactProperties
 *
 * @param email - contact email
 * @param name  - contact name
 * @param url   - organization url
 */
public record OpenApiInfoContactProperties(String email, String name, String url) {
}
