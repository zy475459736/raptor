package com.ppdai.framework.raptor.spring.autoconfig.service;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 处理raptor service 方法返回值
 *
 * @author yinzuolong
 */
public class RaptorReturnValueHandler implements HandlerMethodReturnValueHandler {


    /**
     * TODO 返回值类是被@RaptorMessage注解返回true
     *
     * @param returnType
     * @return
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {

        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        //TODO 处理@RaptorMessage
    }
}
