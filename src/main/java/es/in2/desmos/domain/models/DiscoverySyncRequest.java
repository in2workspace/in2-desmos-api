package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public record DiscoverySyncRequest(
        @JsonProperty("issuer") @NotBlank @URL String issuer,
        @JsonProperty("external_entity_ids") @NotNull List<IdRecord> externalEntityIds) {

    public List<String> createExternalEntityIdsStringList() {
        return externalEntityIds
                .stream()
                .map(IdRecord::id)
                .toList();
    }

    public static List<IdRecord> createExternalEntityIdsListFromString(List<String> idsList) {
        return idsList
                .stream()
                .map(IdRecord::new)
                .toList();
    }
}