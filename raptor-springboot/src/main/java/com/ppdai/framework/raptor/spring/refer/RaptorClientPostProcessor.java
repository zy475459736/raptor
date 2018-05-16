package com.ppdai.framework.raptor.spring.refer;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import com.ppdai.framework.raptor.spring.utils.SpringResourceUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;

import java.lang.reflect.Field;
import java.util.List;

public class RaptorClientPostProcessor implements BeanPostProcessor, ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    @Autowired
    private RaptorClientRegistry raptorClientRegistry;
    @Autowired
    private SpringEnvUrlRepository urlRepository;
    @Autowired
    private ObjectProvider<List<RaptorClientFactory>> raptorClientFactories;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                RaptorClient reference = field.getAnnotation(RaptorClient.class);
                if (reference != null) {
                    if (field.get(bean) != null) {
                        //字段已经初始化
                        continue;
                    }
                    Object value = getClientProxy(field.getType(), reference);
                    if (value != null) {
                        field.set(bean, value);
                    } else {
                        throw new BeanInitializationException(String.format("Can not find Object %s.", bean.getClass().getName()));
                    }
                }
            } catch (Exception e) {
                throw new BeanInitializationException(String.format("Failed to init remote service reference at filed %s in class %s",
                        field.getName(), bean.getClass().getName()), e);
            }
        }
        return bean;
    }

    private Object getClientProxy(Class<?> interfaceClass, RaptorClient reference) {
        RaptorInterface raptorInterface = AnnotationUtils.findAnnotation(interfaceClass, RaptorInterface.class);
        if (raptorInterface == null) {
            return null;
        }
        String url = SpringResourceUtils.resolve(resourceLoader, reference.url());
        URL serviceUrl = this.urlRepository.getUrl(interfaceClass, url);
        if (serviceUrl == null) {
            throw new RuntimeException("Can't find service url for interface: " + interfaceClass);
        }
        Object clientProxy = this.raptorClientRegistry.get(interfaceClass, serviceUrl);
        if (clientProxy != null) {
            return clientProxy;
        }
        List<RaptorClientFactory> factories = this.raptorClientFactories.getIfAvailable();
        for (RaptorClientFactory raptorClientFactory : factories) {
            if (raptorClientFactory.support(interfaceClass, serviceUrl)) {
                clientProxy = raptorClientFactory.create(interfaceClass, serviceUrl);
                this.raptorClientRegistry.registerClientProxy(interfaceClass, clientProxy, serviceUrl);
                break;
            }
        }
        return clientProxy;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
