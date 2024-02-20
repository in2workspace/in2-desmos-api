package es.in2.desmos.blockchain.service.impl;

import es.in2.desmos.blockchain.model.DLTAdapterSubscription;
import es.in2.desmos.blockchain.service.DLTAdapterSubscriptionService;
import es.in2.desmos.blockchain.service.GenericDLTAdapterService;
import es.in2.desmos.blockchain.util.DLTAdapterFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DLTAdapterSubscriptionServiceImpl implements DLTAdapterSubscriptionService {

    private final GenericDLTAdapterService evmAdapter;

    public DLTAdapterSubscriptionServiceImpl(DLTAdapterFactory dltAdapterFactory) {
        this.evmAdapter = dltAdapterFactory.getEVMAdapter();
    }

    @Override
    public Mono<Void> createSubscription(String processId, DLTAdapterSubscription dltAdapterSubscription) {
        return evmAdapter.createSubscription(processId, dltAdapterSubscription);
    }

}
