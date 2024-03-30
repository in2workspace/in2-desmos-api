//package es.in2.desmos.todo.domain.util;
//
//import es.in2.desmos.domain.exceptions.HashLinkException;
//import es.in2.desmos.domain.utils.ApplicationUtils;
//import org.junit.jupiter.api.Test;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Modifier;
//import java.security.NoSuchAlgorithmException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ApplicationUtilsTest {
//
//    @Test
//    void testPrivateConstructor() throws Exception {
//        Constructor<ApplicationUtils> constructor = ApplicationUtils.class.getDeclaredConstructor();
//        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor is not private");
//        constructor.setAccessible(true); // make the constructor accessible
//        // Catch the exception and assert on the message
//        try {
//            constructor.newInstance();
//            fail("Expected an IllegalStateException to be thrown");
//        } catch (InvocationTargetException ite) {
//            assertInstanceOf(IllegalStateException.class, ite.getCause());
//            assertEquals("Utility class", ite.getCause().getMessage());
//        }
//    }
//
//    @Test
//    void testCalculateHashLink() throws NoSuchAlgorithmException {
//        // Test data
//        String expectedHash = "cbb5d4ada62f263a6c653fc123e09dccb652d55baa1fec215bf03f81d76b97af";
//        String previousHash = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
//        String entityHash = "f8638b979b2f4f793ddb6dbd197e0ee25a7a6ea32b0ae22f5e3c5d119d839e75";
//        // Act
//        String result = ApplicationUtils.calculateHashLink(previousHash, entityHash);
//        // Assert
//        assertEquals(expectedHash, result);
//    }
//
//    @Test
//    void testExtractEntityHashFromDataLocation() {
//        String dataLocation = "https://example.com/entity?hl=hash123";
//        String hash = ApplicationUtils.extractHashLinkFromDataLocation(dataLocation);
//        assertEquals("hash123", hash);
//    }
//
//    @Test
//    void testExtractEntityIdFromDataLocation() {
//        String dataLocation = "https://example.com/entities/entityId123?hl=hash";
//        String entityId = ApplicationUtils.extractEntityIdFromDataLocation(dataLocation);
//        assertEquals("entityId123", entityId);
//    }
//
//    @Test
//    void testExtractEntityUrlFromDataLocation() {
//        String dataLocation = "https://example.com/entity?hl=hash123";
//        String url = ApplicationUtils.extractContextBrokerUrlFromDataLocation(dataLocation);
//        assertEquals("https://example.com/entity", url);
//    }
//
//    @Test
//    void testHasHlParameterWithValidUrl() {
//        String url = "https://example.com/entity?hl=hash123";
//        assertTrue(ApplicationUtils.checkIfHashLinkExistInDataLocation(url));
//    }
//
//    @Test
//    void testHasHlParameterWithoutHl() {
//        String url = "https:///badurl";
//        assertThrows(HashLinkException.class, () -> ApplicationUtils.checkIfHashLinkExistInDataLocation(url));
//    }
//
//    @Test
//    void testHasHlParameterWithMalformedUrl() {
//        String url = "badurl";
//        assertThrows(HashLinkException.class, () -> ApplicationUtils.checkIfHashLinkExistInDataLocation(url));
//    }
//
//}
