package es.in2.desmos.api.util;

import es.in2.desmos.api.exception.HashLinkException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationUtilsTest {

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<ApplicationUtils> constructor = ApplicationUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor is not private");
        constructor.setAccessible(true); // make the constructor accessible

        // Catch the exception and assert on the message
        try {
            constructor.newInstance();
            fail("Expected an IllegalStateException to be thrown");
        } catch (InvocationTargetException ite) {
            assertInstanceOf(IllegalStateException.class, ite.getCause());
            assertEquals("Utility class", ite.getCause().getMessage());
        }
    }

    @Test
    void testExtractEntityHashFromDataLocation() {
        String dataLocation = "http://example.com/entity?hl=hash123";
        String hash = ApplicationUtils.extractEntityHashFromDataLocation(dataLocation);
        assertEquals("hash123", hash);
    }

    @Test
    void testExtractEntityIdFromDataLocation() {
        String dataLocation = "http://example.com/entities/entityId123?hl=hash";
        String entityId = ApplicationUtils.extractEntityIdFromDataLocation(dataLocation);
        assertEquals("entityId123", entityId);
    }

    @Test
    void testExtractEntityUrlFromDataLocation() {
        String dataLocation = "http://example.com/entity?hl=hash123";
        String url = ApplicationUtils.extractEntityUrlFromDataLocation(dataLocation);
        assertEquals("http://example.com/entity", url);
    }

    @Test
    void testHasHlParameterWithValidUrl() {
        String url = "http://example.com/entity?hl=hash123";
        assertTrue(ApplicationUtils.hasHlParameter(url));
    }

    @Test
    void testHasHlParameterWithoutHl() {
        String url = "http:///badurl";
        assertThrows(HashLinkException.class, () -> ApplicationUtils.hasHlParameter(url));
    }

    @Test
    void testHasHlParameterWithMalformedUrl() {
        String url = "badurl";
        assertThrows(HashLinkException.class, () -> ApplicationUtils.hasHlParameter(url));
    }

}