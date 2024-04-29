package es.in2.desmos.domain.models;

import es.in2.desmos.objectmothers.EntitySyncRequestMother;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EntitySyncRequestTests {

    @Test
    void testEqualsDifferentObjectWithSameDataIsTrue() {
        EntitySyncRequest originalElement = EntitySyncRequestMother.simple1and2();
        EntitySyncRequest otherElement = EntitySyncRequestMother.simple1and2();

        boolean result = originalElement.equals(otherElement);

        assertTrue(result);
    }

    @Test
    void testEqualsSameObjectTrue() {
        EntitySyncRequest originalElement = EntitySyncRequestMother.simple1and2();


        //noinspection EqualsWithItself
        boolean result = originalElement.equals(originalElement);

        //noinspection ConstantValue
        assertTrue(result);
    }

    @Test
    void testEqualsDifferentObjectIsFalse() {
        EntitySyncRequest originalElement = EntitySyncRequestMother.simple1and2();
        EntitySyncRequest otherElement = EntitySyncRequestMother.simple3and4();

        boolean result = originalElement.equals(otherElement);

        assertFalse(result);
    }

    @Test
    void testEqualsNullObjectIsFalse() {
        EntitySyncRequest originalElement = EntitySyncRequestMother.simple1and2();
        EntitySyncRequest otherElement = null;

        //noinspection ConstantValue
        boolean result = originalElement.equals(otherElement);

        //noinspection ConstantValue
        assertFalse(result);
    }

    @Test
    void testEqualsDifferentObjectTypeIsFalse() {
        EntitySyncRequest originalElement = EntitySyncRequestMother.simple1and2();
        Entity otherElement = new Entity("");


        //noinspection EqualsBetweenInconvertibleTypes
        boolean result = originalElement.equals(otherElement);

        assertFalse(result);
    }


    @Test
    void testHashCode() {
        EntitySyncRequest element = EntitySyncRequestMother.simple1and2();
        int expectedHashCode = Arrays.hashCode(element.entities());

        int result = element.hashCode();

        assertEquals(expectedHashCode, result);
    }

    @Test
    void testToString() {
        EntitySyncRequest element = EntitySyncRequestMother.simple1and2();
        String expectedString = "EntitySyncRequest{entities=[MVEntity4DataNegotiation[id=urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb, type=ProductOffering, version=1.2, lastUpdate=2024-09-05T12:00:00Z, hash=89e62d6be87fd39dc19dc69a35d58d1ac2351854bf48a8264bf075643c89eddf, hashlink=fa54230df0a3a28e89ea00cc647ad5dace35bb80e94207072cd9e9cc01df7912f652], MVEntity4DataNegotiation[id=urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87, type=ProductOffering, version=2.5, lastUpdate=2024-07-09T12:00:00Z, hash=08b236983ba01bbcd268793b104917f89f0bba8160d2f693911087c72b9a8051, hashlink=fa5408b236983ba01bbcd268793b104917f89f0bba8160d2f693911087c72b9a8051]]}";

        String result = element.toString();

        assertEquals(expectedString, result);
    }
}