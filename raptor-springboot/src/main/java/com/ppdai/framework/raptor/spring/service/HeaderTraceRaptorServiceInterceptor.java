package com.ppdai.framework.raptor.spring.service;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.rpc.RaptorContext;
import com.ppdai.framework.raptor.util.NetUtils;
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
        Map<String, String> headers = getRequestHeaders(request);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            //remoteRequestAttachments
            RaptorContext.getContext().putRemoteRequestAttachment(entry.getKey(), entry.getValue());

            //requestAttachments
            if (entry.getKey().toLowerCase().startsWith(ParamNameConstants.TRACE_HEADER_PREFIX)) {
                RaptorContext.getContext().putRequestAttachment(entry.getKey(), entry.getValue());
            }
        }

        putResponseHeaders(response);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        putResponseHeaders(response);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        putResponseHeaders(response);
    }

    protected void putResponseHeaders(HttpServletResponse response) {
        response.addHeader(ParamNameConstants.HOST_SERVER, NetUtils.getLocalIp());

        //remoteResponseAttachments，trace头
        Map<String, String> remoteResponseAttachments = RaptorContext.getContext().getRemoteResponseAttachments();
        if (remoteResponseAttachments != null) {
            for (Map.Entry<String, String> entry : remoteResponseAttachments.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(ParamNameConstants.TRACE_HEADER_PREFIX)) {
                    response.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }

        //responseAttachments，自定义头
        Map<String, String> responseAttachments = RaptorContext.getContext().getRequestAttachments();
        if (responseAttachments != null) {
            for (Map.Entry<String, String> entry : responseAttachments.entrySet()) {
                response.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    protected Map<String, String> getRequestHeaders(HttpServletRequest httpRequest) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headName = headerNames.nextElement();
            String headerValue = httpRequest.getHeader(headName);
            headers.putIfAbsent(headName, headerValue);
        }
        return headers;
    }
}
