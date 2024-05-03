package es.in2.desmos.domain.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ApplicationConstantsTests {

    @Mock
    private ApplicationConstants applicationConstants;

    @Test
    void testHashPrefix() {
        assertEquals("0x", ApplicationConstants.HASH_PREFIX);
    }

    @Test
    void testHashLinkPrefix() {
        assertEquals("?hl=", ApplicationConstants.HASHLINK_PREFIX);
    }

    @Test
    void testSubscriptionIdPrefix() {
        assertEquals("urn:ngsi-ld:Subscription:", ApplicationConstants.SUBSCRIPTION_ID_PREFIX);
    }

    @Test
    void testSubscriptionType() {
        assertEquals("Subscription", ApplicationConstants.SUBSCRIPTION_TYPE);
    }

}