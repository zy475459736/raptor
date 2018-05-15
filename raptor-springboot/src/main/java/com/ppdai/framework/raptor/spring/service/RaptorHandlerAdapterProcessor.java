package com.ppdai.framework.raptor.spring.service;

import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
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
@Configuration
@Slf4j
public class RaptorHandlerAdapterProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (RequestMappingHandlerAdapter.class.isAssignableFrom(ClassUtils.getUserClass(bean))) {
            RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;

            HttpMessageConverter<?> raptorMessageConverter = new RaptorMessageConverter();
            List<HttpMessageConverter<?>> converters = Collections.singletonList(raptorMessageConverter);

            ArrayList<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>(adapter.getArgumentResolvers());
            argumentResolvers.add(0, new RaptorControllerMethodProcessor(converters));
            adapter.setArgumentResolvers(argumentResolvers);

            ArrayList<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>(adapter.getReturnValueHandlers());
            returnValueHandlers.add(0, new RaptorControllerMethodProcessor(converters));
            adapter.setReturnValueHandlers(returnValueHandlers);
        }
        return bean;
    }
}
