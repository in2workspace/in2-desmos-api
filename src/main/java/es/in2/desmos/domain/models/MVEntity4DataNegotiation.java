package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

public record MVEntity4DataNegotiation(
        String id,
        String type,
        String version,
        String lastUpdate,
        String lifecycleStatus,
        String startDateTime,
        String hash,
        String hashlink) {

    @JsonIgnore
    public Float getFloatVersion() {
        String versionValue = version.startsWith("v") ? version.substring(1) : version;
        return Float.parseFloat(versionValue);
    }

    @JsonIgnore
    public Instant getInstantLastUpdate() {
        return Instant.parse(lastUpdate);
    }
}