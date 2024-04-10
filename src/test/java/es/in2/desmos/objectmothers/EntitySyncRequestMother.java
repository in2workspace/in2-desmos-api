package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.EntitySyncRequest;
import es.in2.desmos.domain.models.IdRecord;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class EntitySyncRequestMother {
    private EntitySyncRequestMother() {
    }

    public static @NotNull EntitySyncRequest simpleEntitySyncRequest() {
        List<IdRecord> idRecords = new ArrayList<>();
        idRecords.add(new IdRecord(ProductOfferingMother.sample1().id()));
        idRecords.add(new IdRecord(ProductOfferingMother.sample2().id()));
        return new EntitySyncRequest(idRecords);
    }
}
