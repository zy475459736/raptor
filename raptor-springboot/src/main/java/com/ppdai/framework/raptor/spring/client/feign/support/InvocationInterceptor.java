package com.ppdai.framework.raptor.spring.client.feign.support;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yinzuolong
 */
public interface InvocationInterceptor {

    boolean preHandle(Method method, Object[] args, List<String> preHandleResult);

    void postHandle(Method method, Object[] args, Object result) throws Exception;

    void afterCompletion(Method method, Object[] args, Object result, Exception ex) throws Exception;
}
