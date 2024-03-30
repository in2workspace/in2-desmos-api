//package es.in2.desmos.todo.domain.util;
//
//import es.in2.desmos.domain.utils.MessageUtils;
//import org.junit.jupiter.api.Test;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Modifier;
//
//import static org.junit.jupiter.api.Assertions.*;
//class MessageUtilsTest {
//
//    @Test
//    void testPrivateConstructor() throws Exception {
//        Constructor<MessageUtils> constructor = MessageUtils.class.getDeclaredConstructor();
//        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor is not private");
//        constructor.setAccessible(true); // make the constructor accessible
//        assertThrows(InvocationTargetException.class, constructor::newInstance, "Constructor invocation should throw IllegalStateException");
//    }
//
//    @Test
//    void testConstantValues() {
//        assertEquals("ProcessId: {}, Resource not found", MessageUtils.RESOURCE_NOT_FOUND_MESSAGE);
//        assertEquals("ProcessId: {}, Unauthorized access", MessageUtils.UNAUTHORIZED_ACCESS_MESSAGE);
//        // Add assertions for other constants as well
//    }
//
//}
