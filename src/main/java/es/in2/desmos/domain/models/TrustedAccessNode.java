package es.in2.desmos.domain.models;

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
    private String name;
    private String publicKey;
    private String url;
    private String dltAddress;
}

