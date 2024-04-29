package es.in2.desmos.objectmothers;

public final class BrokerDataMother {

    private BrokerDataMother() {
    }

    public final static String getEntityRequestBrokerJson =
            """
                    [
                        {
                            "id": "urn:productOffering:537e1ee3-0556-4fff-875f-e55bb97e7ab0",
                            "type": "productOffering",
                            "productOfferingPrice": {
                                "type": "Relationship",
                                "object": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a"
                            }
                        },
                        {
                            "id": "urn:productOffering:06f56a54-9be9-4d45-bae7-2a036b721d27",
                            "type": "productOffering",
                            "productOfferingPrice": {
                                "type": "Relationship",
                                "object": "urn:productOfferingPrice:a395344e-2c29-4d36-8463-0c0412f024d7"
                            }
                        },
                        {
                            "id": "urn:productOffering:e8b7e5a7-5d0f-4c9b-b1e5-9b1af474207f",
                            "type": "productOffering",
                            "productOfferingPrice": {
                                "type": "Relationship",
                                "object": "urn:productOfferingPrice:cf36a34a-4e43-453c-bf8b-4a926ed59a0c"
                            }
                        },
                        {
                            "id": "urn:productOffering:d1c34fc5-0c2b-4022-94ab-d7cb99d8edc2",
                            "type": "productOffering",
                            "productOfferingPrice": {
                                "type": "Relationship",
                                "object": "urn:productOfferingPrice:ca9b5de4-bf5f-45de-8b33-0f2518f40e69"
                            }
                        },
                        {
                            "id": "urn:productOffering:39e31a28-583b-4f0d-80c6-6d7600cc9e36",
                            "type": "productOffering",
                            "productOfferingPrice": {
                                "type": "Relationship",
                                "object": "urn:productOfferingPrice:faa692c0-1662-4fe2-b4e3-2d5ad86b47a1"
                            }
                        },
                        {
                            "id": "urn:productOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a",
                            "type": "productOfferingPrice",
                            "price": {
                                "type": "Relationship",
                                "object": "urn:price:2d5f3c16-4e77-45b3-8915-3da36b714e7b"
                            }
                        },
                        {
                            "id": "urn:productOfferingPrice:a395344e-2c29-4d36-8463-0c0412f024d7",
                            "type": "productOfferingPrice",
                            "price": {
                                "type": "Relationship",
                                "object": "urn:price:6380d7c9-d9ec-4d35-865b-76e72d081cbf"
                            }
                        },
                        {
                            "id": "urn:productOfferingPrice:cf36a34a-4e43-453c-bf8b-4a926ed59a0c",
                            "type": "productOfferingPrice",
                            "price": {
                                "type": "Relationship",
                                "object": "urn:price:ab87d164-3a6c-4b61-9b40-6f615b96d35d"
                            },
                    		"priceAlteration": {
                                "type": "Relationship",
                                "object": "urn:priceAlteration:1bcaf091-16e3-4bbc-9800-a4636596384e"
                            }
                        },
                        {
                            "id": "urn:productOfferingPrice:ca9b5de4-bf5f-45de-8b33-0f2518f40e69",
                            "type": "productOfferingPrice",
                            "price": {
                                "type": "Relationship",
                                "object": "urn:price:21e7f562-f62d-41b7-8243-1241d0f871c2"
                            }
                        },
                        {
                            "id": "urn:productOfferingPrice:faa692c0-1662-4fe2-b4e3-2d5ad86b47a1",
                            "type": "productOfferingPrice",
                            "price": {
                                "type": "Relationship",
                                "object": "urn:price:5a1e08b4-eb32-4b68-af44-aa35e6a40fb9"
                            }
                        },
                        {
                            "id": "urn:price:2d5f3c16-4e77-45b3-8915-3da36b714e7b",
                            "type": "price"
                        },
                        {
                            "id": "urn:price:6380d7c9-d9ec-4d35-865b-76e72d081cbf",
                            "type": "price"
                        },
                        {
                            "id": "urn:price:ab87d164-3a6c-4b61-9b40-6f615b96d35d",
                            "type": "price"
                        },
                    	{
                            "id": "urn:priceAlteration:1bcaf091-16e3-4bbc-9800-a4636596384e",
                            "type": "price"
                        },
                        {
                            "id": "urn:price:21e7f562-f62d-41b7-8243-1241d0f871c2",
                            "type": "price"
                        },
                        {
                            "id": "urn:price:5a1e08b4-eb32-4b68-af44-aa35e6a40fb9",
                            "type": "price"
                        }
                    ]""";
}
