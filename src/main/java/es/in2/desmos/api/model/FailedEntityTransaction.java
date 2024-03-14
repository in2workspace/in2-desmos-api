package es.in2.desmos.api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table("failed_entity_transactions")
public class FailedEntityTransaction implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("transaction_id")
    private String transactionId;

    @Column("created_at")
    private Timestamp createdAt;

    @Column("notification_id")
    private long notificationId;

    @Column("entity_id")
    private String entityId;

    @Column("datalocation")
    private String datalocation;

    @Column("entity_type")
    private String entityType;

    @Column("previous_entity_hash")
    private String previousEntityHash;

    @Column("entity")
    private String entity;

    @Column("timestamp")
    private long timestamp;

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
