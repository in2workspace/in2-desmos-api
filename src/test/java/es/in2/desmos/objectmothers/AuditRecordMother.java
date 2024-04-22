package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.AuditRecord;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class AuditRecordMother {
    private AuditRecordMother() {
    }

    public static @NotNull List<AuditRecord> list3And4() {
        List<AuditRecord> auditRecordList = new ArrayList<>();
        var sample3 = MVEntity4DataNegotiationMother.sample3();
        var sample4 = MVEntity4DataNegotiationMother.sample4();
        auditRecordList.add(AuditRecord.builder()
                .entityId(sample3.id())
                .entityHash(sample3.hash())
                .entityHashLink(sample3.hashlink())
                .build());
        auditRecordList.add(AuditRecord.builder()
                .entityId(sample4.id())
                .entityHash(sample4.hash())
                .entityHashLink(sample4.hashlink())
                .build());
        return auditRecordList;
    }
}
