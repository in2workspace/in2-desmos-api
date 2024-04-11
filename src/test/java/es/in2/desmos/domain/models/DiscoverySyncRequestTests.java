package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscoverySyncRequestTests {
    @Test
    void itShouldCreateExternalEntityIdsStringListFromExternalEntityIds() {

        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("3");

        List<IdRecord> idRecords = new ArrayList<>();
        for (var value : expected) {
            idRecords.add(new IdRecord(value));
        }

        DiscoverySyncRequest discoverySyncRequest = new DiscoverySyncRequest("issuer", idRecords);

        var result = discoverySyncRequest.createExternalEntityIdsStringList();

        assertEquals(expected, result);
    }

    @Test
    void itShouldCreateExternalEntityIdsListFromString() {

        List<IdRecord> expected = new ArrayList<>();
        expected.add(new IdRecord("1"));
        expected.add(new IdRecord("2"));
        expected.add(new IdRecord("3"));

        List<String> ids = new ArrayList<>();
        for (var value: expected){
            ids.add(value.id());
        }

        List<IdRecord> result = DiscoverySyncRequest.createExternalEntityIdsListFromString(ids);

        assertEquals(expected, result);
    }
}
