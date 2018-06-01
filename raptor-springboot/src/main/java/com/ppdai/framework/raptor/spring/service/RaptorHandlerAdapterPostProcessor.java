package com.ppdai.framework.raptor.spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yinzuolong
 */
@Slf4j
public class RaptorHandlerAdapterPostProcessor implements BeanPostProcessor {

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (RequestMappingHandlerAdapter.class.isAssignableFrom(ClassUtils.getUserClass(bean))) {
            RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;

            HttpMessageConverter<?> raptorMessageConverter = null;
            for (HttpMessageConverter<?> convert : adapter.getMessageConverters()) {
                if (convert instanceof RaptorMessageConverter) {
                    raptorMessageConverter = convert;
                    break;
                }
            }

            raptorMessageConverter = raptorMessageConverter == null ? new RaptorMessageConverter() : raptorMessageConverter;
            List<HttpMessageConverter<?>> converters = Collections.singletonList(raptorMessageConverter);

            ArrayList<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>(adapter.getArgumentResolvers());
            argumentResolvers.add(0, new RaptorHandlerMethodProcessor(converters));
            adapter.setArgumentResolvers(argumentResolvers);

            ArrayList<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>(adapter.getReturnValueHandlers());
            returnValueHandlers.add(0, new RaptorHandlerMethodProcessor(converters));
            adapter.setReturnValueHandlers(returnValueHandlers);
        }
        return bean;
    }
}
