package com.ppdai.framework.raptor.spring.client.feign;


import com.ppdai.framework.raptor.client.ApacheHttpClientManager;
import com.ppdai.framework.raptor.spring.client.feign.support.RaptorMessageDecoder;
import com.ppdai.framework.raptor.spring.client.feign.support.RaptorMessageEncoder;
import com.ppdai.framework.raptor.spring.client.feign.support.SpringMvcContract;
import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import com.ppdai.framework.raptor.spring.properties.ApacheHttpClientProperties;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RaptorFeignClientAutoConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Bean
    public RaptorFeignClientSpringFactory createRaptorClientFeignSpringFactory(
            Encoder encoder,
            Decoder decoder,
            ErrorDecoder errorDecoder,
            Contract contract,
            Client client,
            Retryer retryer,
            ObjectProvider<List<RequestInterceptor>> requestInterceptors,
            Request.Options options) {
        RaptorFeignClientSpringFactory factory = new RaptorFeignClientSpringFactory();
        factory.setEncoder(encoder);
        factory.setDecoder(decoder);
        factory.setErrorDecoder(errorDecoder);
        factory.setContract(contract);
        factory.setClient(client);
        factory.setRetryer(retryer);
        factory.setRequestInterceptors(requestInterceptors.getIfAvailable());
        factory.setOptions(options);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public Decoder feignDecoder() {
        return new RaptorMessageDecoder(this.messageConverters, new RaptorMessageConverter());
    }

    @Bean
    @ConditionalOnMissingBean
    public Encoder feignEncoder() {
        return new RaptorMessageEncoder(this.messageConverters, new RaptorMessageConverter());
    }

    @Bean
    @ConditionalOnMissingBean
    public Contract feignContract() {
        return new SpringMvcContract(this.parameterProcessors);
    }

    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    //TODO 设置错误解码器
    @Bean
    @ConditionalOnMissingBean
    public ErrorDecoder createFeignErrorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Configuration
    @EnableConfigurationProperties({ApacheHttpClientProperties.class})
    @ConditionalOnProperty(name = "raptor.feign.client", havingValue = "apache", matchIfMissing = true)
    public static class ApacheClientConfig {

        @Autowired
        private ApacheHttpClientProperties apacheHttpClientProperties;

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(name = "raptor.feign.client", havingValue = "apache", matchIfMissing = true)
        public Client createApacheHttpClient(ApacheHttpClientManager feignApacheClientManager) {
            return new ApacheHttpClient(feignApacheClientManager.getHttpClient());
        }

        @Bean
        public ApacheHttpClientManager createFeignApacheClientBuilder() {
            ApacheHttpClientManager feignApacheClientManager = new ApacheHttpClientManager();
            BeanUtils.copyProperties(apacheHttpClientProperties, feignApacheClientManager);
            feignApacheClientManager.init();
            return feignApacheClientManager;
        }

        @Bean
        @ConditionalOnMissingBean
        public Request.Options createOptions() {
            return new Request.Options(apacheHttpClientProperties.getConnectTimeout(), this.apacheHttpClientProperties.getSocketTimeout());
        }

    }
}
