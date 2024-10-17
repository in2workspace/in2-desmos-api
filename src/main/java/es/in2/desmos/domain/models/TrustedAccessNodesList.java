package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/*
 *
 * An attempt has been made to create a record.
 * Error serializing with Snakeyaml dependency.
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrustedAccessNodesList {
    @JsonProperty("organizations")
    private List<TrustedAccessNode> organizations;
}