package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.in2.desmos.domain.models.BrokerEntityValidFor;
import es.in2.desmos.domain.models.BrokerEntityWithIdTypeLastUpdateAndVersion;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public final class MVBrokerEntity4DataNegotiationMother {
    private MVBrokerEntity4DataNegotiationMother() {
    }

    public static @NotNull List<BrokerEntityWithIdTypeLastUpdateAndVersion> list3And4() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        List<BrokerEntityWithIdTypeLastUpdateAndVersion> mVEntity4DataNegotiationList = new ArrayList<>();
        var sample3 = MVEntity4DataNegotiationMother.sample3();
        var sample4 = MVEntity4DataNegotiationMother.sample4();
        mVEntity4DataNegotiationList.add(new BrokerEntityWithIdTypeLastUpdateAndVersion(sample3.id(), sample3.type(), sample3.version(), sample3.lastUpdate(), sample3.lifecycleStatus(), new BrokerEntityValidFor(sample4.startDateTime())));
        mVEntity4DataNegotiationList.add(new BrokerEntityWithIdTypeLastUpdateAndVersion(sample4.id(), sample4.type(), sample4.version(), sample4.lastUpdate(), sample4.lifecycleStatus(), new BrokerEntityValidFor(sample4.startDateTime())));
        return mVEntity4DataNegotiationList;
    }

    public static @NotNull List<BrokerEntityWithIdTypeLastUpdateAndVersion> listCategories() {
        var category1 = MVEntity4DataNegotiationMother.category1();
        var category2 = MVEntity4DataNegotiationMother.category2();
        return List.of(
                new BrokerEntityWithIdTypeLastUpdateAndVersion(category1.id(), category1.type(), category1.version(), category1.lastUpdate(), category1.lifecycleStatus(), new BrokerEntityValidFor(category1.startDateTime())),
                new BrokerEntityWithIdTypeLastUpdateAndVersion(category2.id(), category2.type(), category2.version(), category2.lastUpdate(), category2.lifecycleStatus(), new BrokerEntityValidFor(category2.startDateTime()))
        );
    }

    public static @NotNull List<BrokerEntityWithIdTypeLastUpdateAndVersion> listCatalogs() {
        var catalog1 = MVEntity4DataNegotiationMother.catalog1();
        var catalog2 = MVEntity4DataNegotiationMother.catalog2();
        return List.of(
                new BrokerEntityWithIdTypeLastUpdateAndVersion(catalog1.id(), catalog1.type(), catalog1.version(), catalog1.lastUpdate(), catalog1.lifecycleStatus(), new BrokerEntityValidFor(catalog1.startDateTime())),
                new BrokerEntityWithIdTypeLastUpdateAndVersion(catalog2.id(), catalog2.type(), catalog2.version(), catalog2.lastUpdate(), catalog2.lifecycleStatus(), new BrokerEntityValidFor(catalog2.startDateTime()))
        );
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
                        x.lastUpdate(),
                        x.lifecycleStatus(),
                        new BrokerEntityValidFor(x.startDateTime())
                ))
                .toList();
    }
}
