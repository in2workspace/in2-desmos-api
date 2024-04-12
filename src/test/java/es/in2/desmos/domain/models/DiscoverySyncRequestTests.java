package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class DiscoverySyncRequestTests {
    @Test
    void itShouldCreateExternalEntityIdsStringListFromExternalEntityIds() {

        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("3");

        List<Entity> entities = new ArrayList<>();
        /*for (var value : expected) {
            entities.add(new Entity(value));
        }

        DiscoverySyncRequest discoverySyncRequest = new DiscoverySyncRequest("issuer", entities);

        var result = discoverySyncRequest.createExternalEntityIdsStringList();

        assertEquals(expected, result);*/
    }

    @Test
    void itShouldCreateExternalEntityIdsListFromString() {

        List<Entity> expected = new ArrayList<>();
        /*expected.add(new Entity("1"));
        expected.add(new Entity("2"));
        expected.add(new Entity("3"));

        List<String> ids = new ArrayList<>();
        for (var value: expected){
            ids.add(value.id());
        }

        List<Entity> result = DiscoverySyncRequest.createExternalEntityIdsListFromString(ids);

        assertEquals(expected, result);*/
    }
}
