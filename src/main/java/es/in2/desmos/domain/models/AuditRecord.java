package es.in2.desmos.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("audit_records")
public class AuditRecord implements Persistable<UUID> {

    @Id
    @Column("id")
    @NotNull
    private UUID id;

    @Column("process_id")
    @NotBlank
    private String processId;

    @Column("created_at")
    @NotNull
    private Timestamp createdAt;

    @Column("entity_id")
    @NotBlank
    private String entityId;

    @Column("entity_type")
    @NotBlank
    private String entityType;

    @Column("entity_hash")
    @NotBlank
    private String entityHash;

    @Column("entity_hashlink")
    @NotBlank
    private String entityHashLink;

    @Column("data_location")
    @NotBlank
    @URL
    private String dataLocation;

    @Column("status")
    private AuditRecordStatus status;

    @Column("trader")
    private AuditRecordTrader trader;

    @Column("hash")
    @NotBlank
    private String hash;

    @Column("hashlink")
    @NotBlank
    private String hashLink;

    @Transient
    private boolean newTransaction;

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return this.newTransaction || id == null;
    }

}