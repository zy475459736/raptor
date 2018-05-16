package com.ppdai.framework.raptor.spring.refer.feign;


import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import com.ppdai.framework.raptor.spring.refer.feign.support.RaptorMessageDecoder;
import com.ppdai.framework.raptor.spring.refer.feign.support.RaptorMessageEncoder;
import com.ppdai.framework.raptor.spring.refer.feign.support.SpringMvcContract;
import feign.Client;
import feign.Contract;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RaptorFeignClientAutoConfiguration {

    //TODO 加入raptor自己定义的转换器
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
            ObjectProvider<List<RequestInterceptor>> requestInterceptors) {
        RaptorFeignClientSpringFactory factory = new RaptorFeignClientSpringFactory();
        factory.setEncoder(encoder);
        factory.setDecoder(decoder);
        factory.setErrorDecoder(errorDecoder);
        factory.setContract(contract);
        factory.setClient(client);
        factory.setRetryer(retryer);

        factory.setRequestInterceptors(requestInterceptors.getIfAvailable());
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

    //TODO 共用Apache的client
    @Bean
    @ConditionalOnMissingBean
    public Client createFeignClient() {
        return new ApacheHttpClient();
    }

    //TODO 设置错误解码器
    @Bean
    @ConditionalOnMissingBean
    ErrorDecoder createFeignErrorDecoder() {
        return new ErrorDecoder.Default();
    }


}
