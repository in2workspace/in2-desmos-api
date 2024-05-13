package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class BrokerEntityWithIdTypeLastUpdateAndVersion extends BrokerEntityWithIdAndType {
    @JsonProperty("version")
    private final @NotBlank String version;

    @JsonProperty("lastUpdate")
    private final @NotBlank String lastUpdate;

    public BrokerEntityWithIdTypeLastUpdateAndVersion(String id, String type, String version, String lastUpdate) {
        super(id, type);
        this.version = version;
        this.lastUpdate = lastUpdate;
    }
}
