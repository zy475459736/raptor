package com.ppdai.framework.raptor.spring.endpoint;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.util.Map;

public class RaptorRefersActuatorEndpoint extends AbstractEndpoint {
    private Map<String, Object> clients;

    public RaptorRefersActuatorEndpoint(Map<String, Object> clients) {
        super("RaptorClients", false);
        this.clients = clients;
    }

    @Override
    public Object invoke() {
        return clients;
    }
}
