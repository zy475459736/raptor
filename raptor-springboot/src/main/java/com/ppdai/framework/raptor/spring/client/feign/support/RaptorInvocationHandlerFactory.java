package com.ppdai.framework.raptor.spring.client.feign.support;

import feign.InvocationHandlerFactory;
import feign.Target;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class RaptorInvocationHandlerFactory implements InvocationHandlerFactory {
    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return new RaptorInvocationHandler(target, dispatch);
    }
}
