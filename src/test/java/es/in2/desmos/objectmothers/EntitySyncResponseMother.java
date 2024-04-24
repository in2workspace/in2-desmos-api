package es.in2.desmos.objectmothers;

import org.jetbrains.annotations.NotNull;

public final class EntitySyncResponseMother {
    private EntitySyncResponseMother() {
    }

    public static @NotNull String sample() {
        return """
                [
                     {
                         "id": "urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb",
                         "type": "ProductOffering",
                         "version": "1.2",
                         "lastUpdate": "2024-09-05T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     },
                     {
                         "id": "urn:productOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                         "type": "ProductOffering",
                         "version": "2.5",
                         "lastUpdate": "2024-07-09T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     },
                     {
                         "id": "urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0",
                         "type": "ProductOffering",
                         "version": "4.3",
                         "lastUpdate": "2024-04-03T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     },
                     {
                         "id": "urn:productOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                         "type": "ProductOffering",
                         "version": "1.9",
                         "lastUpdate": "2024-06-02T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     }
                 ]
                """;
    }

    public static @NotNull String sample1and2() {
        return """
                [
                     {
                         "id": "urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb",
                         "type": "productOffering",
                         "version": "1.2",
                         "lastUpdate": "2024-09-05T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     },
                     {
                         "id": "urn:productOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                         "type": "productOffering",
                         "version": "2.5",
                         "lastUpdate": "2024-07-09T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     }
                 ]
                """;
    }

    public static @NotNull String sample1and2Old() {
        return """
                [
                  {
                    "id": "urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb",
                    "type": "ProductOffering",
                    "version": "0",
                    "lastUpdate": "2024-09-05T12:00:00Z",
                    "productSpecification": {
                      "id": "spec-broadband-001",
                      "name": "32Gbps Broadband Spec"
                    },
                    "productOfferingPrice": {
                      "type": "Relationship",
                      "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                    }, "@context": [
                                   "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                               ]
                  },
                  {
                    "id": "urn:productOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                    "type": "ProductOffering",
                    "version": "1",
                    "lastUpdate": "2024-07-09T12:00:00Z",
                    "productSpecification": {
                      "id": "spec-broadband-001",
                      "name": "24Gbps Broadband Spec"
                    },
                    "productOfferingPrice": {
                      "type": "Relationship",
                      "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                    }, "@context": [
                                   "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                               ]
                  }
                ]
                """;
    }

    public static String sample2and4() {
        return """
                [
                     {
                         "id": "urn:productOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87",
                         "type": "ProductOffering",
                         "version": "2.5",
                         "lastUpdate": "2024-07-09T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     },
                     {
                         "id": "urn:productOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c",
                         "type": "ProductOffering",
                         "version": "1.9",
                         "lastUpdate": "2024-06-02T12:00:00Z",
                         "productSpecification": {
                             "id": "spec-broadband-001",
                             "name": "1Gbps Broadband Spec"
                         },
                         "productOfferingPrice": {
                             "type": "Relationship",
                             "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                         },
                         "@context": [
                             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
                         ]
                     }
                 ]
                """;
    }

    public static String getId1() {
        return "urn:productOffering:d86735a6-0faa-463d-a872-00b97affa1cb";
    }

    public static String getId2() {
        return "urn:productOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87";
    }

    public static String getId3() {
        return "urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0";
    }

    public static String getId4() {
        return "urn:productOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c";
    }
}
