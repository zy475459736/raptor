package com.ppdai.framework.raptor.spring.client.feign.support;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.rpc.RaptorContext;
import com.ppdai.framework.raptor.util.RequestIdGenerator;
import feign.Request;
import feign.Response;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author yinzuolong
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HeaderTraceClientInterceptor implements ClientInterceptor {
    @Override
    public boolean preHandle(Request request, Request.Options options, List<String> preHandleResult) {
        //requestId
        request.headers().put(ParamNameConstants.REQUEST_ID, Collections.singleton(RequestIdGenerator.getRequestId()));

        Map<String, String> requestAttachments = RaptorContext.getContext().getRequestAttachments();
        if (requestAttachments != null) {
            for (Map.Entry<String, String> entry : requestAttachments.entrySet()) {
                request.headers().putIfAbsent(entry.getKey(), new ArrayList<>());
                request.headers().get(entry.getKey()).add(entry.getValue());
            }
        }
        return true;
    }

    @Override
    public void postHandle(Request request, Response response) throws Exception {
        Map<String, Collection<String>> headers = response.headers();
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            RaptorContext.getContext().putAttribute(entry.getKey(), entry.getValue());
            if (entry.getKey().toLowerCase().startsWith(ParamNameConstants.TRACE_HEADER_PREFIX)) {
                RaptorContext.getContext().putResponseAttachment(entry.getKey(), CollectionUtils.findFirstMatch(entry.getValue(), entry.getValue()));
            }
        }
    }

    @Override
    public void afterCompletion(Request request, Response response, Exception ex) throws Exception {

    }
}
