package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static es.in2.desmos.domain.utils.ApplicationConstants.HASHLINK_PREFIX;

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
                .dataLocation(baseUri + "/api/v1/entities/" + mvEntity4DataNegotiation.id() + HASHLINK_PREFIX + mvEntity4DataNegotiation.hashlink())
                .status(auditRecordStatus)
                .trader(AuditRecordTrader.CONSUMER)
                .hash("")
                .hashLink("")
                .newTransaction(true)
                .build();
    }

    public static AuditRecord createAuditRecordFromMVAuditServiceEntity4DataNegotiation(String baseUri, MVAuditServiceEntity4DataNegotiation mvAuditServiceEntity4DataNegotiation, AuditRecordStatus auditRecordStatus) {
        String processId = "0";
        return AuditRecord.builder()
                .id(UUID.randomUUID())
                .processId(processId)
                .createdAt(Timestamp.from(Instant.now()))
                .entityId(mvAuditServiceEntity4DataNegotiation.id())
                .entityType(mvAuditServiceEntity4DataNegotiation.type())
                .entityHash(mvAuditServiceEntity4DataNegotiation.hash())
                .entityHashLink(mvAuditServiceEntity4DataNegotiation.hashlink())
                .dataLocation(baseUri + "/api/v1/entities/" + mvAuditServiceEntity4DataNegotiation.id() + HASHLINK_PREFIX + mvAuditServiceEntity4DataNegotiation.hashlink())
                .status(auditRecordStatus)
                .trader(AuditRecordTrader.CONSUMER)
                .hash("")
                .hashLink("")
                .newTransaction(true)
                .build();
    }

    public static @NotNull List<AuditRecord> list3And4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<AuditRecord> auditRecordList = new ArrayList<>();
        var sample3 = MVEntity4DataNegotiationMother.sample3();
        var sample4 = MVEntity4DataNegotiationMother.sample4();
        auditRecordList.add(AuditRecord.builder()
                .entityId(sample3.id())
                .entityType(sample3.type())
                .entityHash(sample3.hash())
                .entityHashLink(sample3.hashlink())
                .build());
        auditRecordList.add(AuditRecord.builder()
                .entityId(sample4.id())
                .entityType(sample4.type())
                .entityHash(sample4.hash())
                .entityHashLink(sample4.hashlink())
                .build());
        return auditRecordList;
    }

    public static @NotNull List<AuditRecord> list3OtherHashAnd4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<AuditRecord> auditRecordList = new ArrayList<>();
        var sample3 = MVEntity4DataNegotiationMother.sample3();
        var sample4 = MVEntity4DataNegotiationMother.sample4();
        auditRecordList.add(AuditRecord.builder()
                .entityId(sample3.id())
                .entityType(sample3.type())
                .entityHash("fjdslkjfdsafjdlskijfasoioiwdshoidsahogiodshgiosdaoi")
                .entityHashLink(sample3.hashlink())
                .build());
        auditRecordList.add(AuditRecord.builder()
                .entityId(sample4.id())
                .entityType(sample4.type())
                .entityHash(sample4.hash())
                .entityHashLink(sample4.hashlink())
                .build());
        return auditRecordList;
    }

    public static @NotNull List<AuditRecord> list3EqualsHashAndHashLinkAnd4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<AuditRecord> auditRecordList = new ArrayList<>();
        var sample3 = MVEntity4DataNegotiationMother.sample3();
        var sample4 = MVEntity4DataNegotiationMother.sample4();
        auditRecordList.add(AuditRecord.builder()
                .entityId(sample3.id())
                .entityType(sample3.type())
                .entityHash(sample3.hash())
                .entityHashLink(sample3.hash())
                .build());
        auditRecordList.add(AuditRecord.builder()
                .entityId(sample4.id())
                .entityType(sample4.type())
                .entityHash(sample4.hash())
                .entityHashLink(sample4.hashlink())
                .build());
        return auditRecordList;
    }

}