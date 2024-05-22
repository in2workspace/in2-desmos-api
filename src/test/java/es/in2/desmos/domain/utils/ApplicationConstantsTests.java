package es.in2.desmos.domain.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConstantsTests {

    @Mock
    private ApplicationConstants applicationConstants;

    @Test
    void testConstructorThrowsException() throws NoSuchMethodException {
        Constructor<ApplicationConstants> constructor = ApplicationConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("Expected an exception to be thrown");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            assertInstanceOf(IllegalStateException.class, e.getCause());
        }
    }

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