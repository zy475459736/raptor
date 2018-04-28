package com.ppdai.framework.raptor.spring.autoconfig.service;

import com.ppdai.framework.raptor.annotation.RaptorMessage;
import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理raptor service 参数转换，service方法必须继承RaptorInterface
 *
 * @author yinzuolong
 */
public class RaptorArgumentResolver implements HandlerMethodArgumentResolver {

    private RaptorMessageConverter raptorMessageConverter;

    RaptorArgumentResolver(RaptorMessageConverter raptorMessageConverter) {
        this.raptorMessageConverter = raptorMessageConverter;
    }

    /**
     * 参数类是被@RaptorMessage注解返回true
     * 接口方法是被@PathVariable注解的返回true
     *
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        RaptorMessage annotation = AnnotationUtils.findAnnotation(parameter.getParameterType(), RaptorMessage.class);
        if (annotation != null) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);
        return raptorMessageConverter.read(parameter.getParameterType(), inputMessage);
    }
}
