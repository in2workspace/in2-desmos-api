package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record MVEntity4DataNegotiation(
        @JsonProperty("id") @NotBlank String id,
        @JsonProperty("type") @NotBlank String type,
        @JsonProperty("version") @NotBlank String version,
        @JsonProperty("lastUpdate") @NotBlank String lastUpdate,
        @JsonProperty("lifecycleStatus") @NotBlank String lifecycleStatus,
        @JsonProperty("validFor") @NotBlank String validFor,
        @JsonProperty("hash") @NotBlank String hash,
        @JsonProperty("hashlink") @NotBlank String hashlink) {

    @JsonIgnore
    public Float getFloatVersion(){
        return Float.parseFloat(version.substring(1));
    }

    @JsonIgnore
    public Instant getInstantLastUpdate(){
        return Instant.parse(lastUpdate);
    }
}
