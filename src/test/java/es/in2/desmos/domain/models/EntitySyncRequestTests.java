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
        String expectedString = "EntitySyncRequest{entities=[MVEntity4DataNegotiation[id=urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb, type=ProductOffering, version=1.2, lastUpdate=2024-09-05T12:00:00Z, hash=ba2aa48e3390a6f39061a8efac7769c3f1c6d642ae83c8ec6d06f837375f17ae, hashlink=fa54ba2aa48e3390a6f39061a8efac7769c3f1c6d642ae83c8ec6d06f837375f17ae], MVEntity4DataNegotiation[id=urn:productOfferingPrice:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87, type=ProductOfferingPrice, version=2.5, lastUpdate=2024-07-09T12:00:00Z, hash=76cbcd6d9338fdd7b9985de08f9823d455e0daabee238cb3349c424dc9c0e8bb, hashlink=fa5476cbcd6d9338fdd7b9985de08f9823d455e0daabee238cb3349c424dc9c0e8bb]]}";

        String result = element.toString();

        assertEquals(expectedString, result);
    }
}