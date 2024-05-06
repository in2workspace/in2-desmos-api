package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.BrokerEntityWithIdTypeLastUpdateAndVersion;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class MVBrokerEntity4DataNegotiationMother {
    private MVBrokerEntity4DataNegotiationMother() {
    }

    public static @NotNull List<BrokerEntityWithIdTypeLastUpdateAndVersion> list3And4() {
        List<BrokerEntityWithIdTypeLastUpdateAndVersion> MVEntity4DataNegotiationList = new ArrayList<>();
        var sample3 = MVEntity4DataNegotiationMother.sample3();
        var sample4 = MVEntity4DataNegotiationMother.sample4();
        MVEntity4DataNegotiationList.add(new BrokerEntityWithIdTypeLastUpdateAndVersion(sample3.id(), sample3.type(), sample3.version(), sample3.lastUpdate()));
        MVEntity4DataNegotiationList.add(new BrokerEntityWithIdTypeLastUpdateAndVersion(sample4.id(), sample4.type(), sample4.version(), sample4.lastUpdate()));
        return MVEntity4DataNegotiationList;
    }

    public static @NotNull List<BrokerEntityWithIdTypeLastUpdateAndVersion> fromList(List<MVEntity4DataNegotiation> originalList) {
        return mvEntitytoBrokerEntity(originalList);
    }

    public static @NotNull List<BrokerEntityWithIdTypeLastUpdateAndVersion> randomList(int size){
        return mvEntitytoBrokerEntity(MVEntity4DataNegotiationMother.randomList(size));
    }

    private static @NotNull List<BrokerEntityWithIdTypeLastUpdateAndVersion> mvEntitytoBrokerEntity(List<MVEntity4DataNegotiation> originalList) {
        return originalList
                .stream()
                .map(x -> new BrokerEntityWithIdTypeLastUpdateAndVersion(
                        x.id(),
                        x.type(),
                        x.version(),
                        x.lastUpdate()
                ))
                .toList();
    }
}
