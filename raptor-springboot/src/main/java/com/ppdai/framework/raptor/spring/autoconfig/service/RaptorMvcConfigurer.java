package com.ppdai.framework.raptor.spring.autoconfig.service;

import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author yinzuolong
 */
@Configuration
public class RaptorMvcConfigurer extends WebMvcConfigurerAdapter {

    private final HttpMessageConverters messageConverters;

    public RaptorMvcConfigurer(HttpMessageConverters messageConverters) {
        this.messageConverters = messageConverters;
    }


    /**
     * 增加一个ArgumentResolver，会添加到RequestMappingHandlerAdapter的customArgumentResolvers列表中，最后会合并到argumentResolvers的后面
     *
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        RaptorArgumentResolver raptorArgumentResolver = new RaptorArgumentResolver(new RaptorMessageConverter());
        argumentResolvers.add(0, raptorArgumentResolver);
    }


    /**
     * TODO 返回值处理器
     * @param returnValueHandlers
     */
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        RaptorReturnValueHandler raptorReturnValueHandler = new RaptorReturnValueHandler();
        returnValueHandlers.add(0, raptorReturnValueHandler);
    }

}
