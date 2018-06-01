package com.ppdai.framework.raptor.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yinzuolong
 */
@Configuration
public class RaptorAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public RaptorMessageConverter createRaptorMessageConverter() {
        ObjectMapper objectMapper = this.applicationContext.getBean(ObjectMapper.class);
        return objectMapper == null ? new RaptorMessageConverter() : new RaptorMessageConverter(objectMapper);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
