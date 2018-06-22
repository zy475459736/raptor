package com.ppdai.framework.raptor.spring.service;

import com.ppdai.framework.raptor.rpc.RaptorContext;
import com.ppdai.framework.raptor.rpc.RaptorRequest;
import com.ppdai.framework.raptor.rpc.RaptorResponse;
import com.ppdai.framework.raptor.spring.utils.RaptorClassUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        if (isRaptorService(handler)) {
            raptorServiceInterceptor.preHandle(RaptorContext.getContext().getRequest(), RaptorContext.getContext().getResponse());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (isRaptorService(handler)) {
            RaptorRequest raptorRequest = RaptorContext.getContext().getRequest();
            RaptorResponse raptorResponse = RaptorContext.getContext().getResponse();
            raptorServiceInterceptor.postHandle(raptorRequest, raptorResponse);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (isRaptorService(handler)) {
            RaptorRequest raptorRequest = RaptorContext.getContext().getRequest();
            RaptorResponse raptorResponse = RaptorContext.getContext().getResponse();
            raptorResponse.setException(ex);
            raptorServiceInterceptor.afterCompletion(raptorRequest, raptorResponse);
        }
    }

    private static boolean isRaptorService(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String interfaceName = RaptorClassUtils.getInterfaceName(ClassUtils.getUserClass(handlerMethod.getBean()), handlerMethod.getMethod());
            return !StringUtils.isEmpty(interfaceName);
        }
        return false;
    }
}
