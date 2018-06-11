package com.ppdai.framework.raptor.spring.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Import({RaptorHandlerMappingPostProcessor.class,
        RaptorHandlerAdapterPostProcessor.class,
        RaptorHandlerMethodProcessor.class})
@Configuration
public class RaptorServiceAutoConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private ObjectProvider<List<RaptorServiceInterceptor>> handlerInterceptors;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<RaptorServiceInterceptor> handlerInterceptorList = handlerInterceptors.getIfAvailable();
        handlerInterceptorList.sort(new AnnotationAwareOrderComparator());
        for (RaptorServiceInterceptor raptorServiceInterceptor : handlerInterceptorList) {
            registry.addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                    return raptorServiceInterceptor.preHandle(request, response, handler);
                }

                @Override
                public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                    raptorServiceInterceptor.postHandle(request, response, handler, modelAndView);
                }

                @Override
                public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                    raptorServiceInterceptor.afterCompletion(request, response, handler, ex);
                }
            });
        }
    }

    @Configuration
    static class HeaderTraceConfig {

        @Bean
        public HeaderTraceRaptorServiceInterceptor createHeaderTraceRaptorServiceInterceptor() {
            return new HeaderTraceRaptorServiceInterceptor();
        }
    }
}
