package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.spring.client.feign.RaptorFeignClientProperties;
import com.ppdai.framework.raptor.spring.client.feign.RaptorFeignClientSpringFactory;
import com.ppdai.framework.raptor.spring.client.feign.support.HeaderTraceClientInterceptor;
import com.ppdai.framework.raptor.spring.client.httpclient.RaptorHttpClientConfiguration;
import com.ppdai.framework.raptor.spring.endpoint.RaptorRefersActuatorEndpoint;
import feign.Client;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.client.HttpClient;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RaptorClientPostProcessor.class, RaptorHttpClientConfiguration.class})
@EnableConfigurationProperties({RaptorFeignClientProperties.class})
public class RaptorClientAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public RaptorFeignClientSpringFactory createRaptorClientFeignSpringFactory() {
        return new RaptorFeignClientSpringFactory();
    }

    @Bean
    public RaptorClientRegistry createClientRegistry() {
        return new RaptorClientRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public Client createRaptorFeignClient() {
        HttpClient httpClient = applicationContext.getBean(HttpClient.class);
        if (httpClient != null) {
            return new ApacheHttpClient(httpClient);
        } else {
            return new ApacheHttpClient();
        }
    }

    @Bean
    public HeaderTraceClientInterceptor createHeaderTraceClientInterceptor() {
        return new HeaderTraceClientInterceptor();
    }

    @Configuration
    @ConditionalOnClass(AbstractEndpoint.class)
    static class ActuatorEndpointConfig {

        @Bean
        public RaptorRefersActuatorEndpoint createRaptorReferActuatorEndpoint(RaptorClientRegistry raptorClientRegistry) {
            return new RaptorRefersActuatorEndpoint(raptorClientRegistry.getAllRegistered());
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
