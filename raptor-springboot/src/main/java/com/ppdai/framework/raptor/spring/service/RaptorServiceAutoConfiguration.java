package com.ppdai.framework.raptor.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Import({RaptorHandlerMappingPostProcessor.class,
        RaptorHandlerAdapterPostProcessor.class,
        RaptorHandlerMethodProcessor.class})
@Configuration
public class RaptorServiceAutoConfiguration {
    @Autowired
    private Environment env;
    
}
