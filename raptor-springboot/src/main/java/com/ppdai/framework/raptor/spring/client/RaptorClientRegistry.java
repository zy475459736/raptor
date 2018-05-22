package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.rpc.URL;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端代理Registry
 */
public class RaptorClientRegistry {

    private Map<String, Object> clientCache = new ConcurrentHashMap<>();

    public void registerClientProxy(Class<?> interfaceClass, Object proxy, URL serviceUrl) {
        Assert.notNull(proxy, "proxy object can't be null.");
        Assert.notNull(serviceUrl, "serviceUrl  can't be null for interface: " + interfaceClass);
        String cacheKey = getCacheKey(interfaceClass.getName(), serviceUrl);
        clientCache.put(cacheKey, proxy);
    }

    public Object get(Class<?> interfaceClass, URL serviceUrl) {
        return clientCache.get(getCacheKey(interfaceClass.getName(), serviceUrl));
    }

    public Map<String, Object> getAllRegistered() {
        return Collections.unmodifiableMap(clientCache);
    }

    private String getCacheKey(String interfaceName, URL serviceUrl) {
        return interfaceName + "@" + serviceUrl.toFullStr();
    }
}
