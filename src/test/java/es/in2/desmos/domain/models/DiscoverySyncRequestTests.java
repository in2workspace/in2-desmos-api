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

        List<MVEntity4DataNegotiation> entities = new ArrayList<>();
        /*for (var value : expected) {
            mvEntities4DataNegotiation.add(new Entity(value));
        }

        DiscoverySyncRequest discoverySyncRequest = new DiscoverySyncRequest("issuer", mvEntities4DataNegotiation);

        var result = discoverySyncRequest.createExternalEntityIdsStringList();

        assertEquals(expected, result);*/
    }

    @Test
    void itShouldCreateExternalEntityIdsListFromString() {

        List<MVEntity4DataNegotiation> expected = new ArrayList<>();
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
