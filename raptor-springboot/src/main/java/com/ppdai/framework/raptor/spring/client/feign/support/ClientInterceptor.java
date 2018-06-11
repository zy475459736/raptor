package com.ppdai.framework.raptor.spring.client.feign.support;

import feign.Request;
import feign.Response;

import java.util.List;

/**
 * @author yinzuolong
 */
public interface ClientInterceptor {

    boolean preHandle(Request request, Request.Options options, List<String> preHandleResult);

    void postHandle(Request request, Response response) throws Exception;

    void afterCompletion(Request request, Response response, Exception ex) throws Exception;
}
