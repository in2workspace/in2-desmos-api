package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class TrustedAccessNode {
    @JsonProperty("name")
    private String name;

    @JsonProperty("publicKey")
    private String publicKey;

    @JsonProperty("url")
    private String url;

    @JsonProperty("dlt_address")
    private String dltAddress;
}

