package com.ppdai.framework.raptor.spring.client.feign;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.spring.client.RaptorClientFactory;
import com.ppdai.framework.raptor.spring.client.feign.support.RaptorMessageDecoder;
import com.ppdai.framework.raptor.spring.client.feign.support.RaptorMessageEncoder;
import com.ppdai.framework.raptor.spring.client.feign.support.SpringMvcContract;
import com.ppdai.framework.raptor.spring.client.httpclient.RaptorHttpClientProperties;
import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import feign.Feign;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.slf4j.Slf4jLogger;
import org.apache.http.client.HttpClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

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
                .logger(new Slf4jLogger(type));

        HttpClient httpClient = getOptional(HttpClient.class);
        if (httpClient != null) {
            builder.client(new RaptorFeignHttpClient(httpClient));
        } else {
            builder.client(new RaptorFeignHttpClient());
        }

        configureUsingApplicationContext(builder);
        configureUsingProperties(type, builder);

        return builder.target(type, getUrl(type));
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

    protected void configureUsingApplicationContext(Feign.Builder builder) {

        RaptorHttpClientProperties httpClientProperties = getOptional(RaptorHttpClientProperties.class);
        if (httpClientProperties != null) {
            builder.options(new Request.Options(httpClientProperties.getConnectionTimeout(), httpClientProperties.getSocketTimeout()));
        }

        Map<String, RequestInterceptor> requestInterceptors = applicationContext.getBeansOfType(RequestInterceptor.class);
        if (requestInterceptors != null && requestInterceptors.size() > 0) {
            builder.requestInterceptors(requestInterceptors.values());
        }
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
