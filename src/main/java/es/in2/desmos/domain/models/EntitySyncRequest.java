package es.in2.desmos.domain.models;

import java.util.Arrays;

public record EntitySyncRequest(Id[] entities) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitySyncRequest entitySyncRequest = (EntitySyncRequest) o;
        return Arrays.equals(entities, entitySyncRequest.entities);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(entities);
    }

    @Override
    public String toString() {
        return "EntitySyncRequest{" +
                "entities=" + Arrays.toString(entities) +
                '}';
    }
}
