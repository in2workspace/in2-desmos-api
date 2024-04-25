package es.in2.desmos.domain.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("blockchain_notification_recover")
public class BlockchainNotificationRecover implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("process_id")
    private String processId;

    @Column("notification_id")
    private String notificationId;

    @Column("publisherAddress")
    private String publisherAddress;

    @Column("eventType")
    private String eventType;

    @Column("timestamp")
    private long timestamp;

    @Column("dataLocation")
    private String dataLocation;

    @Column("relevantMetadata")
    private String relevantMetadata;

    @Column("entityIdHash")
    private String entityIdHash;

    @Column("previousEntityHash")
    private String previousEntityHash;

    @Column("eventQueuePriority")
    private String eventQueuePriority;

    @Transient
    private boolean newBlockchainNotificationRecover;

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return this.newBlockchainNotificationRecover || id == null;
    }

}
