package es.in2.desmos.domain.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

class MessageUtilsTests {

    @Test
    void testConstructorThrowsException() throws NoSuchMethodException {
        Constructor<MessageUtils> constructor = MessageUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("Expected an exception to be thrown");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            assertInstanceOf(IllegalStateException.class, e.getCause());
        }
    }
}