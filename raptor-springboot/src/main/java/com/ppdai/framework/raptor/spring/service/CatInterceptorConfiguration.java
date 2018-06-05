package com.ppdai.framework.raptor.spring.service;

import com.dianping.cat.Cat;
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
@ConditionalOnProperty(value = "raptor.cat.enabled", havingValue = "true", matchIfMissing = true)
public class CatInterceptorConfiguration {

    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnClass(Cat.class)
    static class CatWebResourceConfiguration extends WebMvcConfigurerAdapter {
        @Bean
        CatInterceptor raptorCatInterceptor() {
            return new CatInterceptor();
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(raptorCatInterceptor());
        }
    }
}
