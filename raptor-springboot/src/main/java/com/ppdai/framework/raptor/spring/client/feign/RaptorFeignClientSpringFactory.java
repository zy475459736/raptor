package com.ppdai.framework.raptor.spring.client.feign;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.spring.client.RaptorClientFactory;
import com.ppdai.framework.raptor.spring.client.feign.support.*;
import com.ppdai.framework.raptor.spring.client.httpclient.RaptorHttpClientProperties;
import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class RaptorFeignClientSpringFactory extends RaptorClientFactory.BaseFactory implements ApplicationContextAware {

    private static final String LIBRARY = "spring";

    private ApplicationContext applicationContext;

    @Override
    public boolean support(Class<?> type) {
        return LIBRARY.equalsIgnoreCase(getLibrary(type));
    }

    @Override
    public <T> T create(Class<T> type) {
        RaptorMessageConverter raptorMessageConverter = getOrInstantiate(RaptorMessageConverter.class);

        Feign.Builder builder = Feign.builder()
                .encoder(new RaptorMessageEncoder(raptorMessageConverter))
                .decoder(new RaptorMessageDecoder(raptorMessageConverter))
                .contract(new SpringMvcContract())
                .retryer(Retryer.NEVER_RETRY)
                .logger(new Slf4jLogger(type))
                .options(createOptions())
                .requestInterceptors(getList(RequestInterceptor.class));

        //自定义InvocationHandlerFactory，用于自定义拦截器
        builder.invocationHandlerFactory(createInvocationHandlerFactory());

        //设置client
        builder.client(createRaptorFeignClient());

        //自定义配置
        configureUsingProperties(type, builder);

        return builder.target(type, getUrl(type));
    }

    protected Request.Options createOptions() {
        RaptorHttpClientProperties httpClientProperties = getOptional(RaptorHttpClientProperties.class);
        if (httpClientProperties != null) {
            return new Request.Options(httpClientProperties.getConnectionTimeout(), httpClientProperties.getReadTimeout());
        }
        return new Request.Options();
    }

    protected InvocationHandlerFactory createInvocationHandlerFactory() {
        return new InvocationHandlerFactory() {
            @Override
            public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
                RaptorInvocationHandler invocationHandler = new RaptorInvocationHandler(target, dispatch);
                invocationHandler.setInvocationInterceptors(getList(InvocationInterceptor.class));
                return invocationHandler;
            }
        };
    }

    protected RaptorFeignClient createRaptorFeignClient() {
        RaptorFeignClient raptorFeignClient = new RaptorFeignClient(get(Client.class));
        List<ClientInterceptor> ClientInterceptors = getList(ClientInterceptor.class);
        ClientInterceptors.sort(new AnnotationAwareOrderComparator());
        raptorFeignClient.setClientInterceptors(ClientInterceptors);
        return raptorFeignClient;
    }

    protected String getUrl(Class<?> type) {
        //根据配置的接口找url
        String url = getUrlFromConfig(type.getName());

        if (!StringUtils.hasText(url)) {
            RaptorInterface raptorInterface = AnnotationUtils.findAnnotation(type, RaptorInterface.class);
            //根据配置的appName找url
            url = getUrlFromConfig(raptorInterface.appName());
            if (!StringUtils.hasText(url)) {
                //根据配置的appId找url
                url = getUrlFromConfig(raptorInterface.appId());
            }
        }
        if (!StringUtils.hasText(url)) {
            throw new RuntimeException("Can't find url for interface " + type.getName());
        }
        return url;
    }

    protected String getUrlFromConfig(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        RaptorFeignClientProperties.RaptorClientConfiguration config = getClientConfig(name);
        if (config == null) {
            return null;
        }
        return config.getUrl();
    }

    protected void configureUsingProperties(Class<?> type, Feign.Builder builder) {
        //默认配置
        configureUsingProperties(getClientConfig(null), builder);

        //RaptorInterface注解：appId配置、appName配置
        RaptorInterface raptorInterface = AnnotationUtils.findAnnotation(type, RaptorInterface.class);
        if (raptorInterface != null) {
            if (StringUtils.hasText(raptorInterface.appId())) {
                configureUsingProperties(getClientConfig(raptorInterface.appId()), builder);
            }
            if (StringUtils.hasText(raptorInterface.appName())) {
                configureUsingProperties(getClientConfig(raptorInterface.appName()), builder);
            }
        }

        //接口全名配置
        configureUsingProperties(getClientConfig(type.getName()), builder);
    }

    private RaptorFeignClientProperties.RaptorClientConfiguration getClientConfig(String name) {
        RaptorFeignClientProperties properties = get(RaptorFeignClientProperties.class);
        if (!StringUtils.hasText(name)) {
            name = properties.getDefaultConfig();
        }
        return properties.getConfig().get(name);
    }

    protected void configureUsingProperties(RaptorFeignClientProperties.RaptorClientConfiguration config, Feign.Builder builder) {
        if (config == null) {
            return;
        }

        if (config.getLoggerLevel() != null) {
            builder.logLevel(config.getLoggerLevel());
        }

        if (config.getConnectTimeout() != null && config.getReadTimeout() != null) {
            builder.options(new Request.Options(config.getConnectTimeout(), config.getReadTimeout()));
        }

        if (config.getRetryer() != null) {
            Retryer retryer = getOrInstantiate(config.getRetryer());
            builder.retryer(retryer);
        }

        if (config.getErrorDecoder() != null) {
            ErrorDecoder errorDecoder = getOrInstantiate(config.getErrorDecoder());
            builder.errorDecoder(errorDecoder);
        }

        if (config.getRequestInterceptors() != null && !config.getRequestInterceptors().isEmpty()) {
            // this will add request interceptor to builder, not replace existing
            for (Class<RequestInterceptor> bean : config.getRequestInterceptors()) {
                RequestInterceptor interceptor = getOrInstantiate(bean);
                builder.requestInterceptor(interceptor);
            }
        }

        if (config.getEncoder() != null) {
            builder.encoder(getOrInstantiate(config.getEncoder()));
        }

        if (config.getDecoder() != null) {
            builder.decoder(getOrInstantiate(config.getDecoder()));
        }

        if (config.getContract() != null) {
            builder.contract(getOrInstantiate(config.getContract()));
        }
    }

    private <T> T getOrInstantiate(Class<T> tClass) {
        try {
            return applicationContext.getBean(tClass);
        } catch (NoSuchBeanDefinitionException e) {
            return BeanUtils.instantiateClass(tClass);
        }
    }

    protected <T> List<T> getList(Class<T> type) {
        Map<String, T> map = applicationContext.getBeansOfType(type);
        if (map != null) {
            return new ArrayList<>(applicationContext.getBeansOfType(type).values());
        }
        return new ArrayList<>();
    }

    protected <T> T get(Class<T> type) {
        T instance = applicationContext.getBean(type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type);
        }
        return instance;
    }

    protected <T> T getOptional(Class<T> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
