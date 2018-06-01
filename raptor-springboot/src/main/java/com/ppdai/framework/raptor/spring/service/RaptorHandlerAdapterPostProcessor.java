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

/**
 * 将RaptorHandlerMethodProcessor设置到RequestMappingHandlerAdapter中，优先处理参数和返回值
 *
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
            //找raptorMessageConverter
            RaptorMessageConverter raptorMessageConverter = null;
            for (HttpMessageConverter<?> convert : adapter.getMessageConverters()) {
                if (convert instanceof RaptorMessageConverter) {
                    raptorMessageConverter = (RaptorMessageConverter) convert;
                    break;
                }
            }
            raptorMessageConverter = raptorMessageConverter == null ? createRaptorMessageConverter() : raptorMessageConverter;
            RaptorHandlerMethodProcessor raptorHandlerMethodProcessor = new RaptorHandlerMethodProcessor(raptorMessageConverter);

            ArrayList<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>(adapter.getArgumentResolvers());
            argumentResolvers.add(0, raptorHandlerMethodProcessor);
            adapter.setArgumentResolvers(argumentResolvers);

            ArrayList<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>(adapter.getReturnValueHandlers());
            returnValueHandlers.add(0, raptorHandlerMethodProcessor);
            adapter.setReturnValueHandlers(returnValueHandlers);
        }
        return bean;
    }

    private RaptorMessageConverter createRaptorMessageConverter() {
        return objectMapper == null ? new RaptorMessageConverter() : new RaptorMessageConverter(objectMapper);
    }
}
