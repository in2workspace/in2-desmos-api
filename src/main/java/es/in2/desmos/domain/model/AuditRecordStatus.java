package es.in2.desmos.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuditRecordStatus {

    RECEIVED("received"),
    CREATED("created"),
    RETRIEVED("retrieved"),
    PUBLISHED("published"),
    DELETED("deleted");

    private final String description;

}
