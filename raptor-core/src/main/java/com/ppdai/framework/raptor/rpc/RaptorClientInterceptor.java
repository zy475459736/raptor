package com.ppdai.framework.raptor.rpc;

/**
 * @author yinzuolong
 */
public interface RaptorClientInterceptor {

    void preHandle(RaptorRequest request, RaptorResponse response);

    void postHandle(RaptorRequest request, RaptorResponse response) throws Exception;

    void afterCompletion(RaptorRequest request, RaptorResponse response) throws Exception;
}
