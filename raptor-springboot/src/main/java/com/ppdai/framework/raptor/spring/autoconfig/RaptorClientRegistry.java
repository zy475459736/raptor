package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.exception.RaptorFrameworkException;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.refer.repository.AbstractUrlRepository;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.util.ReflectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class RaptorClientRegistry {

    private AbstractUrlRepository urlRepository;
    private ReferProxyBuilder referProxyBuilder;

    private Map<String, Object>     clientCache = new ConcurrentHashMap<>();

    public RaptorClientRegistry(AbstractUrlRepository urlRepository, ReferProxyBuilder referProxyBuilder) {
        this.urlRepository = urlRepository;
        this.referProxyBuilder = referProxyBuilder;
    }
    /**
     * 根据 接口 和 url 获取/创建Proxy，将Proxy设置给带有RaptorClient注解的Field
     *
     * */
    public Object getOrCreateClientProxy(String interfaceName, String url) {
        URL serviceUrl = getURL(interfaceName, url);
        if (serviceUrl == null) {
            throw new RaptorServiceException(String.format("Can't find service url of interface: %s", interfaceName));
        }

        String cacheKey = getCacheKey(interfaceName, serviceUrl);
        Object proxy    = clientCache.get(cacheKey);
        if (proxy == null) {
            proxy = createClientProxy(interfaceName, serviceUrl);
            clientCache.put(cacheKey, proxy);
        }
        return proxy;
    }
    /**
     * 根据 interfaceName 和 url 两个String 构建 URL
     *
     * */
    public URL getURL(String interfaceName, String url) {
        URL serviceUrl;
        if (StringUtils.isNotBlank(url)) {
            serviceUrl = URL.valueOf(url);
            if (!serviceUrl.isValid()) {
                throw new RaptorServiceException("url is not valid: " + url);
            }
        } else {
            serviceUrl = urlRepository.getUrl(interfaceName);
        }
        return serviceUrl;
    }

    @SuppressWarnings("unchecked")
    public Object createClientProxy(String interfaceName, URL serviceUrl) {
        Class clazz;
        try {
            clazz = ReflectUtil.forName(interfaceName);
        } catch (ClassNotFoundException e) {
            throw new RaptorFrameworkException("Can't find class " + interfaceName);
        }

        return referProxyBuilder.build(clazz, serviceUrl);
    }

    private String getCacheKey(String interfaceName, URL serviceUrl) {
        return interfaceName + "@" + serviceUrl.toFullStr();
    }
}
