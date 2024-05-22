package es.in2.desmos.domain.utils;

public class ApplicationConstants {

    public static final String HASH_PREFIX = "0x";
    public static final String HASHLINK_PREFIX = "?hl=";
    public static final String SUBSCRIPTION_ID_PREFIX = "urn:ngsi-ld:Subscription:";
    public static final String SUBSCRIPTION_TYPE = "Subscription";

    private ApplicationConstants() {
        throw new IllegalStateException("Utility class");
    }

}
