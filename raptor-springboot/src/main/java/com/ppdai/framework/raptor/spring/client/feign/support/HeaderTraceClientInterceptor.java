package com.ppdai.framework.raptor.spring.client.feign.support;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.rpc.RaptorContext;
import com.ppdai.framework.raptor.util.NetUtils;
import com.ppdai.framework.raptor.util.RequestIdGenerator;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author yinzuolong
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HeaderTraceClientInterceptor implements ClientInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        //requestId
        String requestId = RaptorContext.getContext().getRequestAttachment(ParamNameConstants.REQUEST_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = RequestIdGenerator.getRequestId();
        }
        template.header(ParamNameConstants.REQUEST_ID, requestId);

        //clientIp
        template.header(ParamNameConstants.HOST_CLIENT, NetUtils.getLocalIp());

        //remoteRequestAttachments，传递request头
        Map<String, String> remoteRequestAttachments = RaptorContext.getContext().getRemoteRequestAttachments();
        if (remoteRequestAttachments != null) {
            for (Map.Entry<String, String> entry : remoteRequestAttachments.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(ParamNameConstants.TRACE_HEADER_PREFIX)) {
                    template.header(ParamNameConstants.REQUEST_ID, entry.getValue());
                }
            }
        }

        //requestAttachments
        Map<String, String> requestAttachments = RaptorContext.getContext().getRequestAttachments();
        if (requestAttachments != null) {
            for (Map.Entry<String, String> entry : requestAttachments.entrySet()) {
                template.header(ParamNameConstants.REQUEST_ID, entry.getValue());
            }
        }
    }

    @Override
    public boolean preHandle(Request request, Request.Options options, List<String> preHandleResult) {
        return true;
    }

    @Override
    public void postHandle(Request request, Response response) throws Exception {
        Map<String, Collection<String>> headers = response.headers();
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            String value = entry.getValue().iterator().next();

            //remoteResponseAttachment
            RaptorContext.getContext().putRemoteResponseAttachment(entry.getKey(), value);

            //responseAttachment，传递Response头
            if (entry.getKey().toLowerCase().startsWith(ParamNameConstants.TRACE_HEADER_PREFIX)) {
                RaptorContext.getContext().putResponseAttachment(entry.getKey(), value);
            }
        }
    }

    @Override
    public void afterCompletion(Request request, Response response, Exception ex) throws Exception {

    }
}
