package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.spring.client.feign.RaptorFeignClientProperties;
import com.ppdai.framework.raptor.spring.client.feign.RaptorFeignClientSpringFactory;
import com.ppdai.framework.raptor.spring.client.httpclient.RaptorHttpClientConfiguration;
import com.ppdai.framework.raptor.spring.endpoint.RaptorRefersActuatorEndpoint;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RaptorClientPostProcessor.class, RaptorHttpClientConfiguration.class})
@EnableConfigurationProperties({RaptorFeignClientProperties.class})
public class RaptorClientAutoConfiguration {

    @Bean
    public RaptorFeignClientSpringFactory createRaptorClientFeignSpringFactory() {
        return new RaptorFeignClientSpringFactory();
    }

    @Bean
    public RaptorClientRegistry createClientRegistry() {
        return new RaptorClientRegistry();
    }

    @Configuration
    @ConditionalOnClass(AbstractEndpoint.class)
    static class ActuatorEndpointConfig {

        @Bean
        public RaptorRefersActuatorEndpoint createRaptorReferActuatorEndpoint(RaptorClientRegistry raptorClientRegistry) {
            return new RaptorRefersActuatorEndpoint(raptorClientRegistry.getAllRegistered());
        }

    }
}
