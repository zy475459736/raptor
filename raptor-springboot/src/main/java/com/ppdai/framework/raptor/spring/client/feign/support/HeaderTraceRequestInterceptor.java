package com.ppdai.framework.raptor.spring.client.feign.support;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.rpc.RaptorContext;
import com.ppdai.framework.raptor.util.NetUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Map;

/**
 * @author yinzuolong
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HeaderTraceRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        //requestId
        template.header(ParamNameConstants.REQUEST_ID, RaptorContext.getContext().getRequest().getRequestId());

        //clientIp
        template.header(ParamNameConstants.HOST_CLIENT, NetUtils.getLocalIp());

        //传递request头
        Map<String, String> requestAttachments = RaptorContext.getContext().getRequestAttachments();
        if (requestAttachments != null) {
            for (Map.Entry<String, String> entry : requestAttachments.entrySet()) {
                template.header(ParamNameConstants.REQUEST_ID, entry.getValue());
            }
        }

    }
}
