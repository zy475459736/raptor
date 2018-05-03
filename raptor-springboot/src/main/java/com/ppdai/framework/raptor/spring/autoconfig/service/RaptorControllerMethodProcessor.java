package com.ppdai.framework.raptor.spring.autoconfig.service;

import com.ppdai.framework.raptor.annotation.RaptorMessage;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;

import java.util.List;

/**
 * @author yinzuolong
 */
public class RaptorControllerMethodProcessor extends AbstractMessageConverterMethodProcessor {

    public RaptorControllerMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        RaptorMessage annotation = AnnotationUtils.findAnnotation(parameter.getParameterType(), RaptorMessage.class);
        return annotation != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        parameter = parameter.nestedIfOptional();
        //TODO 如果是get方法，将param反射到RaptorMessage中
        return readWithMessageConverters(webRequest, parameter, parameter.getNestedGenericParameterType());
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        RaptorMessage annotation = AnnotationUtils.findAnnotation(returnType.getParameterType(), RaptorMessage.class);
        return annotation != null;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        mavContainer.setRequestHandled(true);
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

        writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
    }
}
