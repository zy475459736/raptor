package com.ppdai.framework.raptor.spring.autoconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author yinzuolong
 */
@Configuration
public class RaptorMvcConfigurer extends WebMvcConfigurerAdapter {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private RaptorServiceConverterMethodProcessor raptorServiceConverterMethodProcessor;

    @PostConstruct
    void init() {
        this.raptorServiceConverterMethodProcessor = new RaptorServiceConverterMethodProcessor(requestMappingHandlerAdapter.getMessageConverters());
    }

    //TODO 参数解析器
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(0, raptorServiceConverterMethodProcessor);
    }

    //TODO 返回值处理器
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(0, raptorServiceConverterMethodProcessor);
    }

    //TODO 配置消息转换器
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }
}
