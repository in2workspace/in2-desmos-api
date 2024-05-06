package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;

import java.util.ArrayList;
import java.util.List;

public final class DataNegotiationResultMother {
    private DataNegotiationResultMother() {
    }

    public static DataNegotiationResult sample(){
        String issuer = "http://example.org";
        List<MVEntity4DataNegotiation> newEntitiesToSync = MVEntity4DataNegotiationMother.list1And2();
        List<MVEntity4DataNegotiation> existingEntitiesToSync = MVEntity4DataNegotiationMother.list3And4();
        return new DataNegotiationResult(issuer, newEntitiesToSync, existingEntitiesToSync);
    }

    public static DataNegotiationResult badHash(){
        String issuer = "http://example.org";
        List<MVEntity4DataNegotiation> newEntitiesToSync = MVEntity4DataNegotiationMother.listbadHash1And2();
        List<MVEntity4DataNegotiation> existingEntitiesToSync = MVEntity4DataNegotiationMother.list3And4();
        return new DataNegotiationResult(issuer, newEntitiesToSync, existingEntitiesToSync);
    }

    public static List<DataNegotiationResult> listNewToSync4AndExistingToSync2(String issuerExistingToSync2, String issuerNewToSync4){
        List<DataNegotiationResult> dataNegotiationResults = new ArrayList<>();
        dataNegotiationResults.add(existingToSync2(issuerExistingToSync2));
        dataNegotiationResults.add(newToSync4(issuerNewToSync4));
        return dataNegotiationResults;
    }


    private static DataNegotiationResult existingToSync2(String issuer){
        List<MVEntity4DataNegotiation> newEntitiesToSync = new ArrayList<>();
        List<MVEntity4DataNegotiation> existingEntitiesToSync = MVEntity4DataNegotiationMother.list2();
        return new DataNegotiationResult(issuer, newEntitiesToSync, existingEntitiesToSync);
    }

    private static DataNegotiationResult newToSync4(String issuer){
        List<MVEntity4DataNegotiation> newEntitiesToSync = MVEntity4DataNegotiationMother.list4();
        List<MVEntity4DataNegotiation> existingEntitiesToSync = new ArrayList<>();
        return new DataNegotiationResult(issuer, newEntitiesToSync, existingEntitiesToSync);
    }




}
