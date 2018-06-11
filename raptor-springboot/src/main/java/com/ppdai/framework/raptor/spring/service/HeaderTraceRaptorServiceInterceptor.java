package com.ppdai.framework.raptor.spring.service;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.rpc.RaptorContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yinzuolong
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HeaderTraceRaptorServiceInterceptor implements RaptorServiceInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> requestAttachments = getAttachments(request);
        for (Map.Entry<String, String> entry : requestAttachments.entrySet()) {
            RaptorContext.getContext().putAttribute(entry.getKey(), entry.getValue());
            if (entry.getKey().toLowerCase().startsWith(ParamNameConstants.TRACE_HEADER_PREFIX)) {
                RaptorContext.getContext().putRequestAttachment(entry.getKey(), entry.getValue());
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Map<String, String> responseAttachments = RaptorContext.getContext().getRequestAttachments();
        if (responseAttachments != null) {
            for (Map.Entry<String, String> entry : responseAttachments.entrySet()) {
                response.addHeader(entry.getKey(), entry.getValue());
            }
        }

    }

    protected Map<String, String> getAttachments(HttpServletRequest httpRequest) {
        Map<String, String> attachments = new HashMap<>();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headName = headerNames.nextElement();
            String headerValue = httpRequest.getHeader(headName);
            attachments.put(headName, headerValue);
        }
        return attachments;
    }
}
