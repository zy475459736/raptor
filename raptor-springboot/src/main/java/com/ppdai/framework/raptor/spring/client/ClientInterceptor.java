package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.rpc.RaptorRequest;
import com.ppdai.framework.raptor.rpc.RaptorResponse;

/**
 * @author yinzuolong
 */
public interface ClientInterceptor {

    void preHandle(RaptorRequest request, RaptorResponse response);

    void postHandle(RaptorRequest request, RaptorResponse response) throws Exception;

    void afterCompletion(RaptorRequest request, RaptorResponse response) throws Exception;
}
