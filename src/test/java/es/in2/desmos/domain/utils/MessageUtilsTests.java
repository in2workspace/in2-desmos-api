package es.in2.desmos.domain.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class MessageUtilsTests {

    @Test
    public void testConstructorThrowsException() {
        try {
            Constructor<MessageUtils> constructor = MessageUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Expected an exception to be thrown");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}