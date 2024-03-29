package es.in2.desmos.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Builder
@Table("failed_event_transactions")
public class FailedEventTransaction implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("entity_id")
    private String entityId;

    @Column("created_at")
    private Timestamp createdAt;

    @Column("datalocation")
    private String datalocation;

    @Column("transaction_id")
    private String transactionId;

    @Column("entity_type")
    private String entityType;

    @Column("iss")
    private String organizationId;

    @Column("previous_entity_hash")
    private String previousEntityHash;

    @Column("priority")
    private EventQueuePriority priority;

    @Transient
    private boolean newTransaction;

    @Override
    @Transient
    public boolean isNew() {
        return this.newTransaction || id == null;
    }

}
