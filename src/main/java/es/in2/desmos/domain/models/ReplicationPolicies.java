package es.in2.desmos.domain.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReplicationPolicies {

    GP_1("lifecycleStatus MUST have a status of Launched, Retired or Obsolete"),
    GP_2(" If a startDateTime is provided, it must be in the past; " +
            "if an endDateTime is provided, it must be in the future.");

    private final String description;
}
