package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.AuditRecordTrader;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AuditRecordMother {
    private AuditRecordMother() {
    }

    public static AuditRecord createAuditRecordFromMVEntity4DataNegotiation(String baseUri, MVEntity4DataNegotiation mvEntity4DataNegotiation, AuditRecordStatus auditRecordStatus) {
        String processId = "0";
        return AuditRecord.builder()
                .id(UUID.randomUUID())
                .processId(processId)
                .createdAt(Timestamp.from(Instant.now()))
                .entityId(mvEntity4DataNegotiation.id())
                .entityType(mvEntity4DataNegotiation.type())
                .entityHash(mvEntity4DataNegotiation.hash())
                .entityHashLink(mvEntity4DataNegotiation.hashlink())
                .dataLocation(baseUri + "/ngsi-ld/v1/entities/" + mvEntity4DataNegotiation.id() + "?" + mvEntity4DataNegotiation.hash())
                .status(auditRecordStatus)
                .trader(AuditRecordTrader.CONSUMER)
                .hash("")
                .hashLink("")
                .newTransaction(true)
                .build();
    }

    public static AuditRecord createAuditRecordFromDataMap(String processId, Map<String, Object> dataMap) {

        return AuditRecord.builder()
                .id(UUID.randomUUID())
                .processId(processId)
                .createdAt(Timestamp.from(Instant.now()))
                .entityId(dataMap.get("id").toString())
                .entityType(dataMap.get("type").toString())
                .entityHash("")
                .entityHashLink("")
                .dataLocation("")
                .status(AuditRecordStatus.RECEIVED)
                .trader(AuditRecordTrader.PRODUCER)
                .hash("")
                .hashLink("")
                .newTransaction(false)
                .build();
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