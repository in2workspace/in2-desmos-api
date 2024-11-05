package es.in2.desmos.objectmothers;

public final class JsonNotificationMother {

    private JsonNotificationMother() {
    }

    public static String getObjectInput() {
        return """
                {
                   "id": "notification:-5106976853901020699",
                   "type": "Notification",
                   "data": [
                     {
                       "id": "urn:ngsi-ld:ProductOffering:122355255",
                       "type": "ProductOffering",
                       "description": {
                         "type": "Property",
                         "value": "Example of a Product offering for cloud services suite"
                       },
                       "notifiedAt": "2024-04-10T11:33:43.807Z"
                     }
                   ],
                   "subscriptionId": "urn:ngsi-ld:Subscription:122355255",
                   "notifiedAt": "2023-03-14T16:38:15.123456Z"
                 }
                """;
    }

    public static String getObjectInputExpected() {
        return """
                {"data":[{"description":{"type":"Property","value":"Example of a Product offering for cloud services suite"},"id":"urn:ngsi-ld:ProductOffering:122355255","notifiedAt":"2024-04-10T11:33:43.807Z","type":"ProductOffering"}],"id":"notification:-5106976853901020699","notifiedAt":"2023-03-14T16:38:15.123456Z","subscriptionId":"urn:ngsi-ld:Subscription:122355255","type":"Notification"}""";
    }

    public static String getArrayInput() {
        return """
                [
                    {
                       "id": "urn:ngsi-ld:ProductOffering:122355256",
                        "description": {
                            "type": "Property",
                            "value": "ProductOffering 2 description"
                        },
                        "type": "ProductOffering",
                        "name": {
                            "type": "Property",
                            "value": "ProductOffering 2"
                        }
                    }
                ]
                """;
    }

    public static String getArrayInputExpected() {
        return """
                [{"description":{"type":"Property","value":"ProductOffering 2 description"},"id":"urn:ngsi-ld:ProductOffering:122355256","name":{"type":"Property","value":"ProductOffering 2"},"type":"ProductOffering"}]""";
    }

    public static String getArrayInput2() {
        return """
                    [
                    {
                        "nombre": "Objeto 1",
                        "id": 1,
                        "descripcion": {
                            "tamaño": "grande",
                            "color": "rojo"
                        },
                        "subelementos": [
                            {
                                "nombre": "Objeto 4",
                                "id": 4,
                                "descripcion": {
                                    "tamaño": "mediano",
                                    "color": "naranja"
                                }
                            },
                            {
                                "nombre": "Objeto 5",
                                "id": 5,
                                "descripcion": {
                                    "tamaño": "pequeño",
                                    "color": "azul"
                                }
                            }
                        ]
                    },
                    {
                        "nombre": "Objeto 2",
                        "id": 2,
                        "descripcion": {
                            "tamaño": "pequeño",
                            "color": "azul"
                        }
                    }
                ]
                """;
    }

    public static String getArrayInput2Expected() {
        return """
                [{"descripcion":{"color":"azul","tamaño":"pequeño"},"id":2,"nombre":"Objeto 2"},{"descripcion":{"color":"rojo","tamaño":"grande"},"id":1,"nombre":"Objeto 1","subelementos":[{"descripcion":{"color":"azul","tamaño":"pequeño"},"id":5,"nombre":"Objeto 5"},{"descripcion":{"color":"naranja","tamaño":"mediano"},"id":4,"nombre":"Objeto 4"}]}]""";
    }

    public static String getPrimitiveInput() {
        return "\"ProductOffering 3\"";
    }

    public static String getPrimitiveInputExpected() {
        return "\"ProductOffering 3\"";
    }
}