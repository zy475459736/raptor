package com.ppdai.framework.raptor.rpc;

/**
 * @author yinzuolong
 */
public interface RaptorServiceInterceptor {

    void preHandle(RaptorRequest request, RaptorResponse response) throws Exception;

    void postHandle(RaptorRequest request, RaptorResponse response) throws Exception;

    void afterCompletion(RaptorRequest request, RaptorResponse response) throws Exception;
}
