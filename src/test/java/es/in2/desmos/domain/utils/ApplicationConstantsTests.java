package es.in2.desmos.domain.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ApplicationConstantsTests {

    @Mock
    private ApplicationConstants applicationConstants;

    @Test
    public void testConstructorThrowsException() {
        try {
            Constructor<ApplicationConstants> constructor = ApplicationConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            Assertions.fail("Expected an exception to be thrown");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Assertions.assertInstanceOf(IllegalStateException.class, e.getCause());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
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