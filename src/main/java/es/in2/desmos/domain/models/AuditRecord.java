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
    @NotNull(message = "id cannot be null")
    private UUID id;

    @Column("process_id")
    @NotBlank(message = "processId cannot be blank")
    private String processId;

    @Column("created_at")
    @NotNull(message = "createdAt cannot be null")
    private Timestamp createdAt;

    @Column("entity_id")
    @NotBlank(message = "entityId cannot be blank")
    private String entityId;

    @Column("entity_type")
    @NotBlank(message = "entityType cannot be blank")
    private String entityType;

    @Column("entity_hash")
    @NotBlank(message = "entityHash cannot be blank")
    private String entityHash;

    @Column("entity_hashlink")
    @NotBlank(message = "entityHashLink cannot be blank")
    private String entityHashLink;

    @Column("data_location")
    @NotBlank(message = "dataLocation cannot be blank")
    @URL(message = "dataLocation must be a valid URL")
    private String dataLocation;

    @Column("status")
    private AuditRecordStatus status;

    @Column("trader")
    private AuditRecordTrader trader;

    @Column("hash")
    @NotBlank(message = "hash cannot be blank")
    private String hash;

    @Column("hashlink")
    @NotBlank(message = "hashLink cannot be blank")
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