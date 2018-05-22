package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.spring.endpoint.RaptorRefersActuatorEndpoint;
import com.ppdai.framework.raptor.spring.properties.ApacheHttpClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import({RaptorClientPostProcessor.class})
public class RaptorClientAutoConfiguration implements EnvironmentAware {

    private Environment environment;

    @Bean
    @ConditionalOnProperty(name = "raptor.urlRepository", havingValue = "springEnv", matchIfMissing = true)
    public SpringEnvUrlRepository createUrlRepository() {
        return new SpringEnvUrlRepository(environment);
    }

    @Bean
    public RaptorClientRegistry createClientRegistry() {
        return new RaptorClientRegistry();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Configuration
    @ConditionalOnClass(AbstractEndpoint.class)
    static class EndpointConfig {

        @Bean
        public RaptorRefersActuatorEndpoint createRaptorReferActuatorEndpoint(RaptorClientRegistry raptorClientRegistry) {
            return new RaptorRefersActuatorEndpoint(raptorClientRegistry.getAllRegistered());
        }

    }
}
