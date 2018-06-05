package com.ppdai.framework.raptor.spring.service;

import com.codahale.metrics.MetricRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author yinzuolong
 */
@Configuration
@ConditionalOnProperty(value = "raptor.metrics.enabled", havingValue = "true", matchIfMissing = true)
public class MetricsInterceptorConfiguration {

    @Configuration
    @ConditionalOnClass(MetricRegistry.class)
    @ConditionalOnWebApplication
    static class MetricsWebResourceConfiguration extends WebMvcConfigurerAdapter {
        @Bean
        MetricsInterceptor raptorMetricsInterceptor() {
            return new MetricsInterceptor();
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(raptorMetricsInterceptor());
        }
    }

}
