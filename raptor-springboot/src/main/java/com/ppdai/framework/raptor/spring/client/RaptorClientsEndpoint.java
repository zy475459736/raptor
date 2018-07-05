package com.ppdai.framework.raptor.spring.client;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

public class RaptorClientsEndpoint extends AbstractEndpoint {
    private RaptorClientRegistry raptorClientRegistry;

    public RaptorClientsEndpoint(RaptorClientRegistry raptorClientRegistry) {
        super("RaptorClients", false);
        this.raptorClientRegistry = raptorClientRegistry;
    }

    @Override
    public Object invoke() {
        return raptorClientRegistry.getAllRegistered();
    }
}
