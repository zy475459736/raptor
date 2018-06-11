package com.ppdai.framework.raptor.spring.service;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yinzuolong
 */
public interface RaptorServiceInterceptor {

    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception;

    void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception;

    void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception;

}
