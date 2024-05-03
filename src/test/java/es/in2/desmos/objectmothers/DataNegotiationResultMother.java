package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.DataNegotiationResult;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;

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
}
