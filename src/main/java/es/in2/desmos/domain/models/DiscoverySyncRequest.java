package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public record DiscoverySyncRequest(
        @JsonProperty("issuer") @NotBlank @URL String issuer,
        @JsonProperty("external_entity_ids") @NotNull List<MVEntity4DataNegotiation> mvEntities4DataNegotiation) {

    public List<String> createExternalEntityIdsStringList() {
        return mvEntities4DataNegotiation
                .stream()
                .map(MVEntity4DataNegotiation::id)
                .toList();
    }
}