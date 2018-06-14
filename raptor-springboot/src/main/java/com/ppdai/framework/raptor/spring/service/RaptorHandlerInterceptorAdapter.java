package com.ppdai.framework.raptor.spring.service;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.rpc.RaptorContext;
import com.ppdai.framework.raptor.rpc.RaptorRequest;
import com.ppdai.framework.raptor.rpc.RaptorResponse;
import com.ppdai.framework.raptor.spring.utils.RaptorClassUtils;
import com.ppdai.framework.raptor.util.NetUtils;
import com.ppdai.framework.raptor.util.ReflectUtil;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class RaptorHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

    private final RaptorServiceInterceptor raptorServiceInterceptor;

    public RaptorHandlerInterceptorAdapter(RaptorServiceInterceptor raptorServiceInterceptor) {
        this.raptorServiceInterceptor = raptorServiceInterceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        initRaptorContext(request, response, handler);
        raptorServiceInterceptor.preHandle(RaptorContext.getContext().getRequest(), RaptorContext.getContext().getResponse());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        RaptorRequest raptorRequest = RaptorContext.getContext().getRequest();
        RaptorResponse raptorResponse = RaptorContext.getContext().getResponse();
        raptorServiceInterceptor.postHandle(raptorRequest, raptorResponse);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RaptorRequest raptorRequest = RaptorContext.getContext().getRequest();
        RaptorResponse raptorResponse = RaptorContext.getContext().getResponse();
        raptorResponse.setException(ex);
        raptorServiceInterceptor.afterCompletion(raptorRequest, raptorResponse);
    }

    protected void initRaptorContext(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (RaptorContext.getContext().getRequest() == null) {
            //初始化
            initRaptorContext(handler);

            //设置request头
            Map<String, String> headers = getRequestHeaders(request);
            RaptorRequest raptorRequest = RaptorContext.getContext().getRequest();
            raptorRequest.setRequestId(headers.get(ParamNameConstants.REQUEST_ID));
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                raptorRequest.setAttachment(entry.getKey(), entry.getValue());

                //传递request头
                if (entry.getKey().toLowerCase().startsWith(ParamNameConstants.TRACE_HEADER_PREFIX)) {
                    RaptorContext.getContext().putRequestAttachment(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    protected void initRaptorContext(Object handler) {
        RaptorRequest raptorRequest = new RaptorRequest();
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String method = ReflectUtil.getMethodSignature(handlerMethod.getMethod());
            String interfaceName = RaptorClassUtils.getInterfaceName(ClassUtils.getUserClass(handlerMethod.getBean()), handlerMethod.getMethod());
            raptorRequest.setMethodName(method);
            raptorRequest.setInterfaceName(interfaceName);
        }

        RaptorResponse raptorResponse = new RaptorResponse();

        RaptorContext.getContext().setRequest(raptorRequest);
        RaptorContext.getContext().setResponse(raptorResponse);
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

    public static void putResponseHeaders(HttpServletResponse response) {
        response.addHeader(ParamNameConstants.HOST_SERVER, NetUtils.getLocalIp());

        RaptorResponse raptorResponse = RaptorContext.getContext().getResponse();
        if (raptorResponse != null && raptorResponse.getAttachments() != null) {
            for (Map.Entry<String, String> entry : raptorResponse.getAttachments().entrySet()) {
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
}
