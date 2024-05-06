package es.in2.desmos.objectmothers;

import org.jetbrains.annotations.NotNull;

public final class EntitySyncResponseMother {
    private EntitySyncResponseMother() {
    }

    public static final @NotNull String sample = """
            [
                 {
                     "id": "urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb",
                     "type": "ProductOffering",
                     "version": "1.2",
                     "lastUpdate": "2024-09-05T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 },
                 {
                     "id": "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                     "type": "ProductOffering",
                     "version": "2.5",
                     "lastUpdate": "2024-07-09T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 },
                 {
                     "id": "urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0",
                     "type": "ProductOffering",
                     "version": "4.3",
                     "lastUpdate": "2024-04-03T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 },
                 {
                     "id": "urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                     "type": "ProductOffering",
                     "version": "1.9",
                     "lastUpdate": "2024-06-02T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 }
             ]
            """;

    public static final String sample2and4 = """
            [
                 {
                     "id": "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                     "type": "ProductOffering",
                     "version": "2.5",
                     "lastUpdate": "2024-07-09T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 },
                 {
                     "id": "urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                     "type": "ProductOffering",
                     "version": "1.9",
                     "lastUpdate": "2024-06-02T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 }
             ]
            """;

    public static final String sample2 = """
            [
                 {
                     "id": "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                     "type": "ProductOffering",
                     "version": "2.5",
                     "lastUpdate": "2024-07-09T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 }
             ]
            """;

    public static final String sample4 = """
            [
                 {
                     "id": "urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                     "type": "ProductOffering",
                     "version": "1.9",
                     "lastUpdate": "2024-06-02T12:00:00Z",
                     "productSpecification": {
                         "id": "spec-broadband-001",
                         "name": "1Gbps Broadband Spec"
                     },
                     "productOfferingPrice": {
                         "type": "Relationship",
                         "object": "urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                     },
                     "@context": [
                         "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                     ]
                 }
             ]
            """;

    public final static String id1 = "urn:ProductOffering:d86735a6-0faa-463d-a872-00b97affa1cb";

    public final static String id2 = "urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87";

    public final static String id3 = "urn:ProductOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0";

    public final static String id4 = "urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c";

}
