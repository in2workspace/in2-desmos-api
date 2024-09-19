package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
@EqualsAndHashCode
public abstract class BrokerEntityWithIdAndType {
    @JsonProperty("id")
    private final @NotBlank String id;

    @JsonProperty("type")
    private final @NotBlank String type;

    protected BrokerEntityWithIdAndType(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
